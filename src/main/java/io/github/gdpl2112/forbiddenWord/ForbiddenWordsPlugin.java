package io.github.gdpl2112.forbiddenWord;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.gdpl2112.database.KlopLocalityDataBase;
import io.github.gdpl2112.database.KlopLocalityDataBaseProxy;
import io.github.gdpl2112.forbiddenWord.mapper.EnableMapper;
import io.github.gdpl2112.forbiddenWord.mapper.ModeMapper;
import io.github.gdpl2112.forbiddenWord.mapper.RecordMapper;
import io.github.gdpl2112.forbiddenWord.mapper.WordMapper;
import io.github.kloping.initialize.FileInitializeValue;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * @author github.kloping
 */
public class ForbiddenWordsPlugin extends JavaPlugin {
    public ForbiddenWordsPlugin() {
        super(new JvmPluginDescriptionBuilder("io.github.gdpl2112.forbiddenWord.ForbiddenWordsPlugin", "0.4.1").info("禁词撤回禁言").build());
    }

    public static final ForbiddenWordsPlugin INSTANCE = new ForbiddenWordsPlugin();
    public static Config config = new Config();
    public static final String PATH = "./conf/forbiddenWord/config.json";

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        config = FileInitializeValue.getValue(PATH, config, true);
        KlopLocalityDataBase.INSTANCE = new KlopLocalityDataBase();
        KlopLocalityDataBase.INSTANCE.setDataFile(config.getDatabase());
        KlopLocalityDataBase.INSTANCE.reload();
        KlopLocalityDataBase.INSTANCE.createAndUseDataBase("db0");
        CommandManager.INSTANCE.registerCommand(CommandLine.INSTANCE, true);
        GlobalEventChannel.INSTANCE.registerListenerHost(new SimpleListenerHost() {
            @Override
            public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
                super.handleException(context, exception);
            }

            @EventHandler
            public void onMessage(GroupMessageEvent event) {
                if (!Work.INSTANCE.isEnable(event.getGroup().getId())) return;
                if (!Work.INSTANCE.hasAdmin(event.getGroup(), event.getSender().getId())) return;
                String text = toText(event.getMessage());
                Work.INSTANCE.word(text.trim(), event);
            }
        });

        Work.INSTANCE.recordMapper = KlopLocalityDataBaseProxy.INSTANCE.generate(RecordMapper.class);
        Work.INSTANCE.modeMapper = KlopLocalityDataBaseProxy.INSTANCE.generate(ModeMapper.class);
        Work.INSTANCE.wordMapper = KlopLocalityDataBaseProxy.INSTANCE.generate(WordMapper.class);
        Work.INSTANCE.enableMapper = KlopLocalityDataBaseProxy.INSTANCE.generate(EnableMapper.class);
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage storage) {
        super.onLoad(storage);
    }


    private String toText(MessageChain m) {
        StringBuilder sb = new StringBuilder();
        for (SingleMessage singleMessage : m) {
            if (singleMessage instanceof PlainText) {
                sb.append(((PlainText) singleMessage).getContent());
            } else if (singleMessage instanceof At) {
                sb.append("[@").append(((At) singleMessage).getTarget()).append("]");
            } else if (singleMessage instanceof Image) {
                if (!config.getOcr()) continue;
                try {
                    Image image = (Image) singleMessage;
                    String url = Image.queryUrl(image);
                    sb.append(getTextFromPic(url));
                } catch (Exception e) {
                    System.err.println("图片识别文字失败");
                }
            }
        }
        return sb.toString().replaceAll("\\s", "").toLowerCase();
    }

    public static final String getTextFromPic(String url) {
        try {
            Connection connection = Jsoup.connect("https://api.wer.plus/api/yocr").method(Connection.Method.POST).ignoreHttpErrors(true).ignoreContentType(true).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36 Edg/108.0.1462.54").requestBody("{\"data\":\"" + url + "\"}");
            Connection.Response response = connection.execute();
            String json = response.body();
            JSONObject jo = JSON.parseObject(json);
            JSONObject data = jo.getJSONObject("data");
            if (!data.containsKey("comment")) return "未能识别出出文字!";
            JSONArray comment = data.getJSONArray("comment");
            StringBuilder sb = new StringBuilder();
            for (Object oe : comment) {
                JSONArray e = (JSONArray) oe;
                sb.append("").append(e.get(1).toString());
            }
            return sb.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
