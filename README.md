# FIChatGPT-Minecraft
- 非本地部署ChatGPT
- 实现在游戏中与ChatGPT进行对话
- 不消耗OpenAI的API
- 使用pandora-chatgpt的httpAPI

## 使用方法:
### 1.安装插件
1.将插件放在服务器文件夹\plugins\内

2.启动服务器等配置生成

#### 默认配置如下
```YAML
##使用要求python环境 安装(pip)pandora
##安装命令 pip install pandora-chatgpt
##服务器启动前启动 pandora
##启动命令 pandora -s 127.0.0.1:8008 -v (地址要和配置一样,参数-v可以不加)
##启动pandora后就能开启服务器了,服务器重启的话是不需要重启pandora的
Prefix: '&7[&3ChatGPT&7]&r'
URL: 'http://127.0.0.1:8008'
##关闭的话所有的提问都会被堆在一起，一个个的回答(建议关闭)
MultitaskConversation: false
##服务器启动的时候清空chatgpt页所有玩家对话数据都会没
CleanUpAtStartup: true
##开启这个的话所有人都用同一个页面,应用的配置是Server:下的配置 关闭的话每个玩家都是独立的一个页面
Current: true
##如果是true那么chatgpt的每一次回答都会进行一次PlayerData数据的同步,如果是false的话只有服务器正常关闭的时候会同步(直接崩的可不算正常)
SaveInRealTime: false
##开启连续对话;单位为秒;ai回复后开始计时;不管那个玩家,只要在gpt回复后指定时间内都可以连续下去(如果关了Current的话每个玩家gpt记忆不同会很怪)
ContinuousDialogue: 10
ChatStart: '@AI'
Format: '&3AI&7: &r{REPLY}'

##Server: #自己设定指定页面
##ParentMessageId: chatgpt最后回复的id
##ConversationId: 页面id
Server:
```
3.关闭服务器
4.安装自己想法修改配置
### 2.安装python
[点击前往Python](https://python.org)
### 3.安装pandora-chatgpt
安装好python后在命令提示符中使用命令
`pip install pandora-chatgpt`
1.安装好后运行pandora
2.更具pandora的时候方式登入好账号或者token(建议账号登入) [pandora](https://github.com/pengzhile/pandora)
3.在登入/配置好后结束运行`pandora -s 127.0.0.1:[插件配置中的端口] -v ` -v插件可选
### 最后启动服务器就能用了
