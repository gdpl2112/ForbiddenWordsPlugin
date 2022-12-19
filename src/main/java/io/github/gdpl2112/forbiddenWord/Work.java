package io.github.gdpl2112.forbiddenWord;

import io.github.gdpl2112.database.e1.QueryWrapper;
import io.github.gdpl2112.forbiddenWord.entity.IllegalWord;
import io.github.gdpl2112.forbiddenWord.entity.Mode;
import io.github.gdpl2112.forbiddenWord.entity.Record;
import io.github.gdpl2112.forbiddenWord.mapper.ModeMapper;
import io.github.gdpl2112.forbiddenWord.mapper.RecordMapper;
import io.github.gdpl2112.forbiddenWord.mapper.WordMapper;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author github.kloping
 */
public class Work {
    public static final QueryWrapper ALL = new QueryWrapper();
    public static final Work INSTANCE = new Work();
    public ModeMapper modeMapper;
    public RecordMapper recordMapper;
    public WordMapper wordMapper;
    public static final String ADD_FAILED = "添加失败";
    public static final String S_NUM = "$num";
    public static final String S_MAX = "$max";

    public String addMode(@NotNull String tips, int n, int t, boolean r) {
        Mode mode = new Mode();
        mode.setTips(tips);
        mode.setN(n);
        mode.setRecall(true);
        Integer t1 = t * 60;
        mode.setT(t1);
        if (modeMapper.insert(mode) > 0) {
            List<Mode> modes = modeMapper.selectByWrapper(new QueryWrapper()
                    .add("tips", tips)
                    .add("n", n)
                    .add("t", t1)
            );
            Mode m = modes.get(0);
            return "添加完成,ID为" + m.getId();
        }
        return ADD_FAILED;
    }

    @NotNull
    public String[] listModes() {
        List<Mode> modes = modeMapper.selectByWrapper(ALL);
        String[] strings = new String[modes.size()];
        int r = 0;
        for (Mode mode : modes) {
            strings[r++] = String.format("%s,'%s'警告%s次禁言%s分钟",
                    mode.getId(), mode.getTips(), mode.getN(), mode.getT() / 60L);
        }
        return strings;
    }

    @NotNull
    public Boolean deleteMode(int id) {
        return modeMapper.deleteById(id) != null;
    }

    @NotNull
    public String addWord(@NotNull String c, int id) {
        IllegalWord word = new IllegalWord();
        word.setC(c);
        word.setMode(id);
        if (wordMapper.insert(word) > 0) {
            List<IllegalWord> words =
                    wordMapper.selectByWrapper(new QueryWrapper().add("c", c).add("mode", id));
            IllegalWord iw = words.get(0);
            return "添加成功;ID为" + iw.getId();
        }
        return ADD_FAILED;
    }

    public boolean deleteWord(int id) {
        return wordMapper.deleteById(id) != null;
    }

    @NotNull
    public String[] listWords() {
        List<IllegalWord> words = wordMapper.selectByWrapper(ALL);
        String[] strings = new String[words.size()];
        int r = 0;
        for (IllegalWord word : words) {
            strings[r++] = String.format("%s,'%s'模式ID:%s",
                    word.getId(), word.getC(), word.getMode());
        }
        return strings;
    }

    private boolean hasAdmin(Group group, long id) {
        NormalMember bot = group.getBotAsMember();
        for (NormalMember m1 : group.getMembers()) {
            if (m1.getId() == id) {
                return bot.getPermission().getLevel() > m1.getPermission().getLevel();
            }
        }
        return bot.getPermission().getLevel() > 0;
    }

    public void word(String text, GroupMessageEvent event) {
        long qid = event.getSender().getId();
        if (!hasAdmin(event.getGroup(), qid)) return;
        for (IllegalWord word : wordMapper.selectByWrapper(ALL)) {
            if (text.contains(word.getC()) || text.equals(word.getC())) {
                Mode mode = modeMapper.selectOneById(word.getMode());
                if (mode == null) continue;
                if (mode.getRecall())
                    MessageSource.recall(event.getSource());
                QueryWrapper qw = new QueryWrapper()
                        .add("qid", qid)
                        .add("wid", word.getId());
                List<Record> records = recordMapper.selectByWrapper(qw);
                Record record = null;
                if (records == null || records.isEmpty()) {
                    record = new Record();
                    record.setQid(qid);
                    record.setWid(word.getId());
                    record.setNum(1);
                    recordMapper.insert(record);
                } else {
                    record = records.get(0);
                    record.setNum(record.getNum() + 1);
                    recordMapper.updateById(record);
                }
                if (record.getNum() >= mode.getN()) {
                    for (NormalMember member : event.getGroup().getMembers()) {
                        if (member.getId() == event.getSender().getId()) {
                            member.mute(mode.getT().intValue());
                        }
                    }
                    recordMapper.deleteById(record.getId());
                }
                MessageChainBuilder builder = new MessageChainBuilder();
                builder.append(new At(qid)).append("\n")
                        .append(mode.getTips()
                                .replace(S_NUM, record.getNum().toString())
                                .replace(S_MAX, mode.getN().toString()));
                event.getGroup().sendMessage(builder.build());
            }
        }
    }
}
