package io.github.gdpl2112.forbiddenWord

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.java.JCompositeCommand

class CommandLine private constructor() : JCompositeCommand(ForbiddenWordsPlugin.INSTANCE, "forbidden") {
    companion object {
        @JvmField
        val INSTANCE = CommandLine()
    }

    init {
        description = "forbidden Word 命令"
    }


    @Description("添加一个模式")
    @SubCommand("addMode")
    suspend fun CommandSender.forbiddenWordsPluginAddMode(
        @Name("触发提示文本") tips: String,
        @Name("警告次数为0时则直接禁言") n: Int,
        @Name("禁言时长/分钟") t: Int,
        @Name("是否撤回true/false") r: Boolean,
    ) {
        sendMessage(Work.INSTANCE.addMode(tips, n, t,r))
    }

    @Description("列出所有模式")
    @SubCommand("listMode")
    suspend fun CommandSender.forbiddenWordsPluginListMode() {
        sendMessage(Work.INSTANCE.listModes().joinToString("\n").trim())
    }

    @Description("删除指定ID模式")
    @SubCommand("deleteMode")
    suspend fun CommandSender.forbiddenWordsPluginDeleteMode(id: Int) {
        if (Work.INSTANCE.deleteMode(id)) {
            forbiddenWordsPluginListMode()
        } else {
            sendMessage("删除失败")
        }
    }

    @Description("删除指定ID模式")
    @SubCommand("deleteWord")
    suspend fun CommandSender.forbiddenWordsPluginDeleteWord(id: Int) {
        if (Work.INSTANCE.deleteWord(id)) {
            forbiddenWordsPluginListWord()
        } else {
            sendMessage("删除失败")
        }
    }

    @Description("列出所有禁词")
    @SubCommand("listWord")
    suspend fun CommandSender.forbiddenWordsPluginListWord() {
        sendMessage(Work.INSTANCE.listWords().joinToString("\n").trim())
    }

    @Description("添加一个禁词")
    @SubCommand("addWord")
    suspend fun CommandSender.forbiddenWordsPluginAddWord(
        @Name("禁词文本") c: String,
        @Name("关联 mode Id") id: Int,
    ) {
        sendMessage(Work.INSTANCE.addWord(c, id))
    }
}