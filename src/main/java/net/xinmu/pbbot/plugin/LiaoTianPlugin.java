package net.xinmu.pbbot.plugin;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import net.xinmu.pbbot.bean.WeiBo;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 点歌插件实现案例
 * api调用网站 ：https://api.iyk0.com/
 * hutool工具包：https://www.hutool.cn/
 */
@Component
public class LiaoTianPlugin extends BotPlugin {
    String prefix = "<light_app content=\"{";
    String suffix = "}>\"";
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String message = event.getRawMessage();
        long groupId = event.getGroupId();
        long selfId = bot.getSelfId();
        String format = String.format("<at qq=\"%d\"/>", selfId);
        if (message.startsWith(format)) {
            try {
                String substring = message.substring(format.length() + 1);
                if(substring.startsWith("斗图")){
                    Msg.builder().image(requestApi(substring)).sendToGroup(bot, groupId);
                }
                else {
                    Msg.builder().text(requestApi(substring)).sendToGroup(bot, groupId);
                }
            } catch (Exception e) {
                Msg.builder().text(requestApi("???")).sendToGroup(bot, groupId);
            }

        }
        return super.onGroupMessage(bot, event);
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {

        String message = event.getRawMessage();
        if (message.startsWith(".")) {
            String substring = message.substring(1);
            Msg.builder().text(requestApi(substring)).sendToFriend(bot, event.getUserId());
        }

        return super.onPrivateMessage(bot, event);
    }

    //调用hutool工具包请求聊天接口
    private String requestApi(String message) {
//        String apiUrl="https://api.iyk0.com/liaotian/?msg=%s";
        String apiUrl = "";
        String res = "";
        String api = "";
        if (message.startsWith("藏头诗 ")){
//            http://api.wpbom.com/api/betan.php?msg=[内容]&a=(0-头，1-尾)&b=(5-五言，7-七言)
            apiUrl = "http://api.wpbom.com/api/betan.php?msg=%s&a=%s&b=%s";
            String[] strings = message.substring(4).split(" ");
            api = String.format(apiUrl, strings[0],strings[1],strings[2]);
            res = HttpUtil.get(api);
            return res;
        }
        else if (message.startsWith("斗图")){
            apiUrl = "http://api.wpbom.com/api/bucket.php?msg=%s";
            api = String.format(apiUrl, message.substring(2));
            String resUrl = HttpUtil.get(api);
            return resUrl;
        }
        else if (message.startsWith("百度热搜")){
            try {
                Integer number = Integer.valueOf(message.substring(4).trim()) -1;
                String baiduUrl = "https://api.iyk0.com/bdr/";
                String baiduRes = HttpUtil.get(baiduUrl);
                JSONObject jsonObject = JSONUtil.parseObj(baiduRes);
                String baiduTitle = JSONUtil.parseObj(jsonObject.getJSONArray("data").get(number)).getStr("title");
                String baiduUrlRes = JSONUtil.parseObj(jsonObject.getJSONArray("data").get(number)).getStr("url").replaceAll("[m\\.baidu\\.com]{11}","www.baidu.com").split("&")[0].replaceAll("%23","");
                return baiduUrlRes;
            } catch (NumberFormatException e) {
                String format = "回复格式不正确,请回复:@机器人百度热搜加空格加数字(1-10)";
//                Msg.builder().text(format).sendToGroup(bot, groupId);
                e.printStackTrace();
                return format;
            }
        }
        else if (message.startsWith("热搜")) {
            try {
                Integer number = Integer.valueOf(message.substring(2).trim()) ;
                String weiboUrl = "https://api.iyk0.com/wbr/";
                String weiboRes = HttpUtil.get(weiboUrl);
                List<WeiBo> weiBos = JSON.parseArray("[" + weiboRes + "]", WeiBo.class);
                String weiboTitle = weiBos.get(number-1).getTitle();
                String weiboUrlRes = weiBos.get(number-1).getUrl().split("&")[0].replaceAll("%23","");
                return weiboUrlRes;
            } catch (NumberFormatException e) {
                String format ="回复格式不正确,请回复:@机器人热搜加空格加数字(1-10)";
//                Msg.builder().text(format).sendToGroup(bot, groupId);
                e.printStackTrace();
                return format;
            }
        }
        else {
            apiUrl = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=%s";
            api = String.format(apiUrl, message);
            res = HttpUtil.get(api);
            if (res.startsWith("{")) {
                return JSONUtil.parseObj(res).getStr("content").replaceAll("[{br}]{4}", "\r\n");
            } else {
                return res;
            }
        }
    }

}
