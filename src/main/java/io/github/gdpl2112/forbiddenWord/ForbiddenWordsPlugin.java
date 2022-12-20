package io.github.gdpl2112.forbiddenWord;

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
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import org.jetbrains.annotations.NotNull;

/**
 * @author github.kloping
 */
public class ForbiddenWordsPlugin extends JavaPlugin {
    public ForbiddenWordsPlugin() {
        super(new JvmPluginDescriptionBuilder("io.github.gdpl2112.forbiddenWord.ForbiddenWordsPlugin",
                "0.2.1")
                .info("禁词撤回禁言").build());
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
        config = FileInitializeValue.getValue(PATH, config);
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
            }
        }
        return sb.toString();
    }
}
