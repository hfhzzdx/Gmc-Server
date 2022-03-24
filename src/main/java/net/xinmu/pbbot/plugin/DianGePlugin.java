package net.xinmu.pbbot.plugin;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import net.xinmu.pbbot.almanac.ProgrammerCalendar;
import net.xinmu.pbbot.baidu.TransApi;
import net.xinmu.pbbot.bean.WeiBo;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;


import java.io.UnsupportedEncodingException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;


/**
 * 多功能插件实现案例
 * api调用网站 ：https://api.iyk0.com/
 * hutool工具包：https://www.hutool.cn/
 */
@Component
public class DianGePlugin extends BotPlugin {
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String message = event.getRawMessage();
        long groupId = event.getGroupId();
        List<Long> list = new ArrayList<Long>();
        list.add(2261273381L);
        list.add(2678885646L);
        list.add(630995935L);
        list.add(1771675301L);
        list.add(444875998L);
        list.add(825135068L);
        list.add(2092138038L);
        list.add(365860593L);
        list.add(785041256L);
        list.add(2367662066L);
        list.add(2582692686L);
        list.add(452682610L);
        list.add(1623325331L);
        list.add(473048656L);
        list.add(3227434025L);
        list.add(760664761L);
        list.add(2248568041L);
        if (message.startsWith("点歌") && message.length() > 2) {
            String miscName = message.substring(2);
            try {
                JSONObject jsonObject = requestApi(miscName.trim());
                String img = jsonObject.getStr("img");
                String song = jsonObject.getStr("song");
                String singer = jsonObject.getStr("singer");
                String url = jsonObject.getStr("url");
                Msg.builder().music(song, singer, url, img, url).sendToGroup(bot, groupId);
            } catch (Exception e) {
                String format = String.format("搜索不到与[%s]的相关歌曲，请稍后重试或换个关键词试试。", miscName.trim());
                Msg.builder().text(format).sendToGroup(bot, groupId);
            }

        }

        if (message.startsWith("网易点歌") && message.length() > 4) {
            String miscName = message.substring(4);
            try {
                JSONObject jsonObject = requestApi19(miscName.trim());
                String img = jsonObject.getStr("img");
                String song = jsonObject.getStr("song");
                String singer = jsonObject.getStr("singer");
                String url = jsonObject.getStr("url");
                Msg.builder().music(song, singer, url, img, url).sendToGroup(bot, groupId);
            } catch (Exception e) {
                String format = String.format("搜索不到与[%s]的相关歌曲，请稍后重试或换个关键词试试。", miscName.trim());
                Msg.builder().text(format).sendToGroup(bot, groupId);
            }

        }

        if (message.startsWith("ping") && message.length() > 4) {
            String urlName = message.substring(4);
            try {
                String res = requestApi14(urlName.trim());
                Msg.builder().text(res).sendToGroup(bot, groupId);
            } catch (Exception e) {
                String format = String.format("[%s]可能不符合格式要求！", urlName.trim());
                Msg.builder().text(format).sendToGroup(bot, groupId);
            }
        }

        if (message.startsWith("备案查询") && message.length() > 4) {
            String urlName = message.substring(4);
            try {
                String res = requestApi15(urlName.trim());
                Msg.builder().text(res).sendToGroup(bot, groupId);
            } catch (Exception e) {
                String format = String.format("[%s]可能不符合格式要求！", urlName.trim());
                Msg.builder().text(format).sendToGroup(bot, groupId);
            }
        }

        if (message.startsWith("在线查询") && message.length() > 4) {
            String urlName = message.substring(4);
            try {
                String res = requestApi16(urlName.trim());
                String img = String.format("https://api.iyk0.com/qqimg?qq=%s", urlName.trim());
                Msg.builder().text("qq:").text(urlName).image(img).text(res).sendToGroup(bot, groupId);
            } catch (Exception e) {
                String format = String.format("[%s]可能不符合格式要求！", urlName.trim());
                Msg.builder().text(format).sendToGroup(bot, groupId);
            }
        }


//        if (message.startsWith("天气")&&message.length()>2){
//            String urlName = message.substring(2);
//            try {
//                String res = requestApi17(urlName.trim());
//                Msg.builder().json(0,res).sendToGroup(bot,groupId);
//            }catch (Exception e){
//                String format = String.format("[%s]可能不符合格式要求！", urlName.trim());
//                Msg.builder().text(format).sendToGroup(bot,groupId);
//            }
//        }


        if (message.startsWith("歌词") && message.length() > 2) {
            String miscDict = message.substring(2);
            JSONObject jsonObject = requestApi2(miscDict.trim(), 1);
            String code = jsonObject.getStr("code");
            if ("200".equals(code)) {
                String Keyword = jsonObject.getStr("Keyword");
                String data = jsonObject.getStr("data").replaceAll("[{br}]{4}", "\r\n");
                Msg.builder().text("歌曲名：").text(Keyword).text("\r\n歌词: \r\n").text(data).sendToGroup(bot, groupId);
            } else {
                Msg.builder().text("请求失败！").sendToGroup(bot, groupId);
            }

        }

        if ("微博热搜".equals(message)) {
            List<WeiBo> weiBos = requestApi4();
            Msg text = Msg.builder().text("微博热搜榜Top10\r\n");
            for (int i = 0; i < weiBos.size(); i++) {
                text.text(String.valueOf(i + 1)).text(".").text(weiBos.get(i).getTitle()).text("\r\n");
                if (i == 9) {
                    break;
                }
            }
            text.sendToGroup(bot, groupId);
        }
        if ("百度热搜".equals(message)) {
            JSONObject jsonObject = requestApi3();
            String code = jsonObject.getStr("code");
            if ("200".equals(code)) {
//                String Keyword = jsonObject.getStr("Keyword");
                JSONArray data = jsonObject.getJSONArray("data");
                Msg text = Msg.builder().text("百度热搜榜Top10\r\n");
                for (int i = 0; i < data.size(); i++) {
                    String title = JSONUtil.parseObj(data.get(i)).getStr("title");
                    text.text(String.valueOf(i + 1)).text(".").text(title).text("\r\n");
                    if (i == 9) {
                        break;
                    }
                }
                text.sendToGroup(bot, groupId);
            } else {
                Msg.builder().text("请求失败！").sendToGroup(bot, groupId);
            }
        }

        if (message.startsWith("星座运势") && message.length() > 4) {
            String msg = message.substring(4);
            JSONObject jsonObject = requestApi10(msg.trim());
            String code = jsonObject.getStr("code");
            if ("200".equals(code)) {
                String data = jsonObject.getStr("data").replaceAll("[{br}]{4}", "\r\n");
                Msg.builder().text(data).sendToGroup(bot, groupId);
            } else {
                Msg.builder().text("请求失败！").sendToGroup(bot, groupId);
            }

        }
        if (message.startsWith("禁言 ")) {
            // 禁言他 @sqlboy @西安-阿赟 100
            //收到群消息 群号：744253964 QQ：452682610 内容：禁言他 <at qq="3385661046"/> <at qq="1248902091"/> 100
            // @qqName    <at qq="1248902091"/>

            Long userId = event.getUserId();      //管理员qq
            System.out.println("本句话的发言人qq为" + userId.toString());
            String msg = event.getRawMessage();
            String[] msg_list = msg.split(" ");
            int duration = Integer.valueOf(msg_list[msg_list.length - 1]).intValue();

            if (list.contains(userId)) {
                Long bannedUserId = 0L;
                Pattern pattern = Pattern.compile("([1-9][0-9]{4,})");
                Matcher matcher = pattern.matcher(msg);
                if (matcher.find()) {
                    bannedUserId = Long.valueOf(matcher.group(0));
                }
                try {
                    bot.setGroupBan(groupId, bannedUserId, duration);
                } catch (Exception e) {
                    e.printStackTrace();
                    Msg.builder().text("对不起,没权限,找大表哥来个钩子权限后才能使用").sendToGroup(bot, groupId);
                }
            } else {
                Msg.builder().text("找群主进行PY交易换取管理员后,再次尝试").sendToGroup(bot, groupId);
            }

//        if(msg.startsWith("<at qq=\"3385661046\"/>")){
//           return ;
//        }


        }


//        if ("骚话".equals(message)) {
//            Msg.builder().text(requestApi5()).sendToGroup(bot,groupId);
//        }
        if ("精神语录".equals(message)) {
            Msg.builder().text(requestApi6()).sendToGroup(bot, groupId);
        }
        if ("毒鸡汤".equals(message)) {
            Msg.builder().text(requestApi7()).sendToGroup(bot, groupId);
        }
        if ("舔狗日记".equals(message)) {
            Msg.builder().text(requestApi8()).sendToGroup(bot, groupId);
        }
        if ("渣男语录".equals(message)||"邓总日记".equals(message)) {
            Msg.builder().text(requestApi9()).sendToGroup(bot, groupId);
        }
        if(message.contains("黄历")){
            ProgrammerCalendar cc = new ProgrammerCalendar();
            int n = 2;
            Msg.builder().text(cc.getOldAlmanac(n)).sendToGroup(bot, groupId);
        }
        if ("伤感语录".equals(message)) {
            Msg.builder().text(requestApi11()).sendToGroup(bot, groupId);
        }
        if ("摸鱼日历".equals(message)) {
            Msg text = Msg.builder();
            text.image("https://api.vvhan.com/api/moyu");
            text.sendToGroup(bot, groupId);
        }
        if (message.contains("看世界")) {
            Msg text = Msg.builder();
            text.image("http://api.weijieyue.cn/api/60s/api.php");
            text.sendToGroup(bot, groupId);
        }
        if (message.endsWith("疫情")) {
            String msg = message.substring(0,message.length()-2);
            JSONObject jsonObject = requestApi34(msg.trim());
            String code = jsonObject.getStr("code");
            if ("200".equals(code)) {
                String city = jsonObject.getStr("查询地区");
                String quezhen = jsonObject.getStr("目前确诊");
                String dead = jsonObject.getStr("死亡人数");
                String zhiyu = jsonObject.getStr("治愈人数");
                String xinzeng = jsonObject.getStr("新增确诊");
                String xiancun = jsonObject.getStr("现存确诊");
                String wuzhengzhuang = jsonObject.getStr("现存无症状");
                String time = jsonObject.getStr("time");
                Msg.builder().text("查询城市: ").text(city).text("\r\n")
                        .text("目前确诊: ").text(quezhen).text("\r\n")
                        .text("死亡人数: ").text(dead).text("\r\n")
                        .text("治愈人数: ").text(zhiyu).text("\r\n")
                        .text("新增确诊: ").text(xinzeng).text("\r\n")
                        .text("现存确诊: ").text(xiancun).text("\r\n")
                        .text("现存无症状: ").text(wuzhengzhuang).text("\r\n")
                        .text("查询时间: ").text(time).text("\r\n")
                        .sendToGroup(bot, groupId);
            } else {
                Msg.builder().text("请求失败！").sendToGroup(bot, groupId);
            }
        }
        if (message.startsWith("油价查询")) {
            String msg = message.substring(4);
            JSONObject jsonObject = requestApi35(msg.trim());
            if ("200".equals(jsonObject.getStr("code"))) {
                String data = jsonObject.getStr("data").substring(1,jsonObject.getStr("data").length()-1).replaceAll("[{]","").replaceAll("[}]","");
                Msg.builder().text("查询省份: ").text(jsonObject.getStr("area")).text("\r\n\t").text(data).sendToGroup(bot, groupId);
            } else {
                Msg.builder().text("请求失败！").sendToGroup(bot, groupId);
            }
        }
//        if (message.startsWith("翻译")) {
//            String msg = message.substring(2);
//            JSONObject jsonObject = requestApi30(msg);
//            String code = jsonObject.getStr("code");
//            if ("200".equals(code)){
//                String data = jsonObject.getStr("fanyi").replaceAll("[{br}]{4}", "\r\n");
//                Msg.builder().text(data).sendToGroup(bot,groupId);
//            }else {
//                Msg.builder().text("请求失败！").sendToGroup(bot,groupId);
//            }
//        }
        if (message.startsWith("翻译")) {
            String msg = message.substring(2);
            JSONObject jsonObject = requestApi30(msg);
//            String code = jsonObject.getStr("dst");
//            String data = jsonObject.getStr("fanyi").replaceAll("[{br}]{4}", "\r\n");
//
            // 返回json数据格式
            // {"trans_result":[{"dst":"Warriors, send more pictures. I have friends who want to see them en","src":"勇士,多发点图,我有朋友也想看 en"}],"from":"zh","to":"en"}
//            Msg.builder().text(jsonObject.toString()).sendToGroup(bot,groupId);
//            String regex = "\\[(.*?)]";
//            Pattern pattern = Pattern.compile(regex);
//            Matcher matcher = pattern.matcher(jsonObject.getStr("trans_result"));
//            String group = matcher.group(0);
            String trans_result = jsonObject.getStr("trans_result").replaceAll("\\[", "").replaceAll("\\]", "");
            JSONObject jsonObject1 = JSONUtil.parseObj(trans_result);
//            System.out.println("jsonObject1="+jsonObject1.toString());
            Msg.builder().text(jsonObject1.getStr("dst")).sendToGroup(bot, groupId);

        }
        if (message.endsWith("天气")) {
            String msg = message.trim();
            JSONObject jsonObject = requestApi31(msg);
            String data = jsonObject.getStr("content");
            String data1 = data.replaceAll("[{br}]{4}", "\r\n");
            Msg.builder().text(data1).sendToGroup(bot, groupId);
        }
        if (message.startsWith("小爱同学")) {
            String msg = message.substring(4).trim();
            JSONObject jsonObject = requestApi32(msg);
//            https://api.iyk0.com/yy/?msg=优客
            String data = jsonObject.getStr("text").replaceAll("[{br}]{4}", "\r\n");
            Long userId = event.getUserId();
            //<at qq="3385661046"/>
//            bot.sendPrivateMsg(userId, data,false);
            String dataRes = "<at qq=\"" + userId + "\"/>" + data;
            Msg.builder().text(dataRes).sendToGroup(bot, groupId);
        }
        if (message.endsWith("怎么办")) {
            String msg = "提问前先Google和百度\n" +
                    "如果连Google和百度都不会用,就别卷了\n" +
                    "【问题描述】，我曾经试着：\n" +
                    "【思路细节】，但是不能得到解决，\n" +
                    "报错如下：【错误截图】，请问该怎么解决？\n" +
                    "你写的函数，对象是为了完成...";

            Msg.builder().text(msg).sendToGroup(bot, groupId);
        }

        if ("彩虹屁".equals(message)) {
            JSONObject jsonObject = requestApi12();
            String code = jsonObject.getStr("code");
            if ("200".equals(code)) {
                String data = jsonObject.getStr("txt").replaceAll("[{br}]{4}", "\r\n");
                Msg.builder().text(data).sendToGroup(bot, groupId);
            } else {
                Msg.builder().text("请求失败！").sendToGroup(bot, groupId);
            }
        }

        if ("笑话".equals(message)) {
            Msg.builder().text(requestApi18()).sendToGroup(bot, groupId);
        }
        if ("散文一则".equals(message)) {
            Msg.builder().text(requestApi36()).sendToGroup(bot, groupId);
        }
        if ("code说明".equals(message)) {
            Msg.builder().text(printCode()).sendToGroup(bot, groupId);
        }

//        if (message.startsWith("搜视频")&&message.length()>3){
//            String msg = message.substring(3);
//            JSONObject jsonObject = requestApi13(msg.trim());
//            String code = jsonObject.getStr("code");
//            if ("200".equals(code)){
//                String img = jsonObject.getStr("img");
//                String url = jsonObject.getStr("url");
//                String type = jsonObject.getStr("type");
//                Msg.builder().video(url,img,true).sendToGroup(bot,groupId);
//            }else {
//                Msg.builder().text("请求失败！").sendToGroup(bot,groupId);
//            }
//        }

//
//        if (message.startsWith("mm")&&message.length()>2){
//            String msg = message.substring(2);
//            Msg text = Msg.builder();
//            try {
//                int i = Integer.parseInt(msg);
//                if (i>2){
//                    Msg.builder().text("数量过多！上限2张图！！").sendToGroup(bot,groupId);
//                    return MESSAGE_IGNORE;
//                }
//                for (int j = 0; j < i; j++) {
//                    text.image("https://api.iyk0.com/mtyh/");
//                }
//                text.sendToGroup(bot,groupId);
//            }catch (Exception e){
//                Msg.builder().text("指令有误!请输入mm图片数量如：mm3").sendToGroup(bot,groupId);
//            }
//
//        }

//        if (message.startsWith("mn")&&message.length()>2){
//            String msg = message.substring(2);
//            Msg text = Msg.builder();
//            try {
//                int i = Integer.parseInt(msg);
//                if (i>2){
//                    Msg.builder().text("数量过多！上限2张图！！").sendToGroup(bot,groupId);
//                    return MESSAGE_IGNORE;
//                }
//                for (int j = 0; j < i; j++) {
//                    text.image("https://api.iyk0.com/mn/2");
//                }
//                text.sendToGroup(bot,groupId);
//            }catch (Exception e){
//                Msg.builder().text("指令有误!请输入mn图片数量如：mn3").sendToGroup(bot,groupId);
//            }
//
//        }

//        if (message.startsWith("cos")&&message.length()>3){
//            String msg = message.substring(3);
//            Msg text = Msg.builder();
//            try {
//                int i = Integer.parseInt(msg);
//                if (i>2){
//                    Msg.builder().text("数量过多！上限2张图！").sendToGroup(bot,groupId);
//                    return MESSAGE_IGNORE;
//                }
//                for (int j = 0; j < i; j++) {
//                    text.image("https://api.iyk0.com/cos/");
//                }
//                text.sendToGroup(bot,groupId);
//            }catch (Exception e){
//                Msg.builder().text("指令有误!请输入cos图片数量如：cos3").sendToGroup(bot,groupId);
//            }
//
//        }

        if ("功能".equals(message) || "帮助".equals(message) || "菜单".equals(message) || "指令".equals(message) || "插件".equals(message)) {
            Msg text = Msg.builder();
//            text.text("1、").text("mm*|mn* *代表输入数字 美图").text("\r\n");
//            text.text("2、").text("cos* *代表输入数字 cosplay图").text("\r\n");
            text.text("3、").text("点歌|网易点歌.  触发: 点歌或网易点歌加空格加歌曲名").text("\r\n");
            text.text("4、").text("搜歌词.         触发: 歌词加空格加任意歌词").text("\r\n");
            text.text("5、").text("百度热搜榜.     触发: 百度热搜.   获取详情请@机器人回复百度热搜加标题ID").text("\r\n");
            text.text("6、").text("微博热搜榜.     触发: 微博热搜.   获取详情请@机器人回复微博热搜加标题ID").text("\r\n");
            text.text("7、").text("精神语录|伤感语录|舔狗日记|彩虹屁|毒鸡汤|渣男语录|笑话  触发:直接输入").text("\r\n");
//            text.text("8、").text("搜视频 可选参数说明：网红、明星、热舞、风景、游戏、动物").text("\r\n");
            text.text("9、").text("星座运势        触发: 星座运势加空格加星座名").text("\r\n");
            text.text("10、").text("ping           触发: ping 加空格加域名").text("\r\n");
            text.text("11、").text("备案查询       触发: 备案查询加空格加域名").text("\r\n");
            text.text("12、").text("查询QQ登录状态 触发: 在线查询加空格加QQ号").text("\r\n");
            text.text("13、").text("中英互译       触发: 翻译加需要翻译的内容加空格加code. 如需查看code说明请输入:code说明").text("\r\n");
            text.text("14、").text("天气预报       触发: 城市名加天气 ").text("\r\n");
            text.text("15、").text("小爱同学       触发: 以小爱同学开头,不显示音频资源 ").text("\r\n");
            text.text("16、").text("60s看世界      触发: 包含'看世界'").text("\r\n");
            text.text("17、").text("禁言群成员     触发: 禁言加空格加@群成员加空格加禁言时间,单位秒").text("\r\n");
            text.text("18、").text("藏头诗         触发: @机器人加空格加藏头诗加msg加空格加0或1加空格加5或7.   (0尾1头,5五言7七言)").text("\r\n");
            text.text("19、").text("斗图           触发: @机器人加斗图加所需文字信息").text("\r\n");
            text.text("20、").text("程序猿黄历     触发: 语句包含老黄历").text("\r\n");
            text.text("21、").text("疫情查询       触发: 城市名加疫情").text("\r\n");
            text.text("22、").text("油价查询       触发: 油价查询加省名").text("\r\n");
            text.text("23、").text("我在人间凑数的日子  触发: 散文一则").text("\r\n");
//            text.text("翻译功能code说明:").text("\r\n").text(printCode());
            text.sendToGroup(bot, groupId);
        }

        return super.onGroupMessage(bot, event);
    }

    // 随机笑话
    // 调用hutool工具包请求
    public String requestApi18() {
        String apiUrl = "https://api.iyk0.com/xh/";
        String res = HttpUtil.get(apiUrl).replaceAll("[{br}]{4}", "\r\n");
        return res;
    }

    /**
     * 天气在线查询
     *
     * @param trim
     * @return
     */
    private String requestApi17(String trim) {
        String apiUrl = "https://api.iyk0.com/tq/?city=%s&type=json";
        String format = String.format(apiUrl, trim);
        String res = HttpUtil.get(format);
        return res;
    }

    /**
     * QQ电脑在线查询
     *
     * @param trim
     * @return
     */
    private String requestApi16(String trim) {
//        https://api.iyk0.com/qqzx/?qq=2822569653
        String apiUrl = "https://api.iyk0.com/qqzx/?qq=%s";
        String format = String.format(apiUrl, trim);
        String res = HttpUtil.get(format);
        return JSONUtil.parseObj(res).getStr("msg").replaceAll("[{br}]{4}", "\r\n");
    }

    /**
     * 网易云音乐查询
     *
     * @param trim
     * @return
     */
    private JSONObject requestApi19(String trim) {
        String apiUrl = "https://api.iyk0.com/wymusic/?msg=%s&n=1";
        String format = String.format(apiUrl, trim);
        String res = HttpUtil.get(format);
        return JSONUtil.parseObj(res);
    }

    /**
     * 备案查询
     *
     * @param trim
     * @return
     */
    private String requestApi15(String trim) {
        String apiUrl = "https://api.iyk0.com/beian/?url=%s";
        String format = String.format(apiUrl, trim);
        String res = HttpUtil.get(format).replaceAll("[{br}]{4}", "\r\n");
        return res;
    }

    /**
     * 在线ping
     *
     * @param trim
     * @return
     */
    private String requestApi14(String trim) {
        String apiUrl = "https://api.iyk0.com/ping/?url=%s";
        String format = String.format(apiUrl, trim);
        String res = HttpUtil.get(format).replaceAll("[{br}]{4}", "\r\n");
        return res;
    }

    // 调用hutool工具包请求
    //https://api.iyk0.com/gcsg/?Keyword=乘坐地铁三号线
    private JSONObject requestApi2(String trim, int i) {
        String apiUrl = "https://api.iyk0.com/gcsg/?Keyword=%s&n=1";
        String api = String.format(apiUrl, trim);
        String res = HttpUtil.get(api);
        JSONObject jsonObject = JSONUtil.parseObj(res);
        return jsonObject;
    }

    // 调用hutool工具包请求
    //https://api.iyk0.com/gcsg/?Keyword=乘坐地铁三号线
    public JSONObject requestApi(String miscName) {
        String apiUrl = "https://api.iyk0.com/qqmusic/?msg=%s&n=1";
        String api = String.format(apiUrl, miscName);
        String res = HttpUtil.get(api);
        JSONObject jsonObject = JSONUtil.parseObj(res);
        return jsonObject;
    }

    // 调用hutool工具包请求
    //百度热搜
    public JSONObject requestApi3() {
        String apiUrl = "https://api.iyk0.com/bdr/";
        String res = HttpUtil.get(apiUrl);
        JSONObject jsonObject = JSONUtil.parseObj(res);
        return jsonObject;
    }

    // 调用hutool工具包请求
    //微博热搜
    public List<WeiBo> requestApi4() {
        String apiUrl = "https://api.iyk0.com/wbr/";
        String res = HttpUtil.get(apiUrl);
        List<WeiBo> weiBos = JSON.parseArray("[" + res + "]", WeiBo.class);
        return weiBos;
    }

    // 随机骚话
    // 调用hutool工具包请求
    public String requestApi5() {
        String apiUrl = "https://api.iyk0.com/sao/";
        String res = HttpUtil.get(apiUrl);

        return res;
    }

    // 精神语录
    // 调用hutool工具包请求
    public String requestApi6() {
        String apiUrl = "https://api.iyk0.com/jsyl/";
        String res = HttpUtil.get(apiUrl).replaceAll("[{br}]{4}", "\r\n");

        return res;
    }

    // 随机毒鸡汤
    // 调用hutool工具包请求
    public String requestApi7() {
        String apiUrl = "https://api.iyk0.com/du/";
        String res = HttpUtil.get(apiUrl);
        String data = JSONUtil.parseObj(res).getStr("data").replaceAll("[{br}]{4}", "\r\n");
        return data;
    }

    // 舔狗日记
    // 调用hutool工具包请求
    public String requestApi8() {
        String apiUrl = "https://api.iyk0.com/tiangou/";
        String res = HttpUtil.get(apiUrl).replaceAll("[{br}]{4}", "\r\n");
        return res;
    }

    // 渣男语录
    // 调用hutool工具包请求
    public String requestApi9() {
        String apiUrl = "https://api.iyk0.com/zhanan/";
        String res = HttpUtil.get(apiUrl).replaceAll("[{br}]{4}", "\r\n");
        return res;
    }

    // 调用hutool工具包请求
    //星座运势
    public JSONObject requestApi10(String msg) {
        String apiUrl = "https://api.iyk0.com/xzys/?msg=%s";
        String api = String.format(apiUrl, msg);
        String res = HttpUtil.get(api);
        JSONObject jsonObject = JSONUtil.parseObj(res);
        return jsonObject;
    }

    // 调用hutool工具包请求
    // 伤感语录
    public String requestApi11() {
        String apiUrl = "https://api.iyk0.com/sg/";
        String res = HttpUtil.get(apiUrl).replaceAll("[{br}]{4}", "\r\n");
        return res;
    }


    //请求调用hutool工具包请求
    public JSONObject requestApi12() {
        String apiUrl = "https://api.iyk0.com/chp/";
        String res = HttpUtil.get(apiUrl);
        return JSONUtil.parseObj(res);
    }

    //请求调用hutool工具包请求
    //精选短视频类型 可选参数说明：网红、明星、热舞、风景、游戏、动物
    public JSONObject requestApi13(String msg) {
        String apiUrl = "https://api.iyk0.com/dsp/?type=%s";
        String api = String.format(apiUrl, msg);
        String res = HttpUtil.get(api);
        return JSONUtil.parseObj(res);
    }

    public JSONObject requestApi30(String msg) {
        // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
        final String APP_ID = "你的appid";
        final String SECURITY_KEY = "你的密钥";
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        String[] s = msg.split(" ");
        String code = s[s.length - 1];
//        String apiUrl="https://api.66mz8.com/api/translation.php?info=%s";
//        String api = String.format(apiUrl, msg);
//        String res = HttpUtil.get(api);
        return JSONUtil.parseObj(api.getTransResult(msg, "auto", code));
    }

    public JSONObject requestApi31(String msg) {
        String apiUrl = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=%s";
        String api = String.format(apiUrl, msg);
        String res = HttpUtil.get(api);
        return JSONUtil.parseObj(res);
    }

    public JSONObject requestApi32(String msg) {
        String apiUrl = "http://api.weijieyue.cn/api/xiaoai/api.php?msg=%s";
        String api = String.format(apiUrl, msg);
        String res = HttpUtil.get(api);
        return JSONUtil.parseObj(res);
    }

    /*
    疫情数据不准确,不做了
     */
    public JSONObject requestApi33(String msg) {
        String apiUrl = "http://111.231.75.86:8000/api/cities/CHN/?cityNames=%s";
        String api = String.format(apiUrl, msg);
        String res = HttpUtil.get(api);
        return JSONUtil.parseObj(res);
    }
    public JSONObject requestApi34(String msg) {
        String apiUrl = "https://api.iyk0.com/yq/?msg=%s";
        String api = String.format(apiUrl, msg);
        String res = HttpUtil.get(api);
        return JSONUtil.parseObj(res);
    }
    public JSONObject requestApi35(String msg) {
        String apiUrl = "https://api.iyk0.com/youjia/?area=%s";
        String api = String.format(apiUrl, msg);
        String res = HttpUtil.get(api);
        return JSONUtil.parseObj(res);
    }
    public String requestApi36() {
        String apiUrl = "https://api.iyk0.com/renjian/";
        String res = HttpUtil.get(apiUrl);
        return res;
    }
    public String shortUrl(String longUrl) {
        String apiUrl = "https://api.uomg.com/api/long2dwz?dwzapi=tcn&url=%s";
        String api = String.format(apiUrl, longUrl);
        String res = HttpUtil.get(api);
        return JSONUtil.parseObj(res).getStr("ae_url");
    }

    public String toXml(JSONObject jsonObject) {
        String img = jsonObject.getStr("img");
        String song = jsonObject.getStr("song");
        String singer = jsonObject.getStr("singer");
        String url = jsonObject.getStr("url");
        String xml = "{\"app\":\"com.tencent.structmsg\",\"config\":{\"autosize\":true,\"ctime\":%d,\"forward\":true,\"token\":\"52399f3861cc5e735ca84c178583fab4\",\"type\":\"normal\"},\"desc\":\"音乐\",\"extra\":{\"app_type\":1,\"appid\":100495085,\"msg_seq\":7051763387857781476,\"uin\":2733363076},\"meta\":{\"music\":{\"action\":\"\",\"android_pkg_name\":\"\",\"app_type\":1,\"appid\":100495085,\"ctime\":1641866609,\"desc\":\"%s\",\"jumpUrl\":\"%s\",\"musicUrl\":\"%s\",\"preview\":\"%s\",\"sourceMsgId\":\"0\",\"source_icon\":\"\",\"source_url\":\"\",\"tag\":\"网易云音乐\",\"title\":\"%s\",\"uin\":2733363076}},\"prompt\":\"[分享]%s\",\"ver\":\"0.0.0.1\",\"view\":\"music\"}";
        String format = String.format(xml, System.currentTimeMillis() / 1000, singer, url, url, img, song, song);
        System.out.println(format);
        return format;
    }

    public String printCode() {
        String a =
                "zh \t中文\n" +
                        "en \t英语\n" +
                        "yue\t粤语\n" +
                        "wyw\t文言文\n" +
                        "jp \t日语\n" +
                        "kor\t韩语\n" +
                        "fra\t法语\n" +
                        "spa\t西班牙语\n" +
                        "th \t泰语\n" +
                        "ara\t阿拉伯语\n" +
                        "ru \t俄语\n" +
                        "pt \t葡萄牙语\n" +
                        "de \t德语\n" +
                        "it \t意大利语\n" +
                        "el \t希腊语\n" +
                        "nl \t荷兰语\n" +
                        "pl \t波兰语\n" +
                        "bul\t保加利亚语\n" +
                        "est\t爱沙尼亚语\n" +
                        "dan\t丹麦语\n" +
                        "fin\t芬兰语\n" +
                        "cs \t捷克语\n" +
                        "rom\t罗马尼亚语\n" +
                        "slo\t斯洛文尼亚语\n" +
                        "swe\t瑞典语\n" +
                        "hu \t匈牙利语\n" +
                        "cht\t繁体中文\n" +
                        "vie\t越南语";
        return a;
    }


    /**
     * @param fromStr : 要转换的原始字符串
     * @return : 得到转换后的字符串
     */
    private String tranUrl(String fromStr) {
        StringBuffer stringBufferResult = new StringBuffer();
        for (int i = 0; i < fromStr.length(); i++) {
            char chr = fromStr.charAt(i);
            if (chr == '%') {
                StringBuffer stringTmp = new StringBuffer();
                stringTmp.append(fromStr.charAt(i + 1)).append(fromStr.charAt(i + 2));
                //转换字符，16进制转换成整型
                stringBufferResult.append((char) (Integer.valueOf(stringTmp.toString(), 16).intValue()));
                i = i + 2;
                continue;
            }
            stringBufferResult.append(chr);
        }

        String newStr = null; //编码转换
        try {
            newStr = new String(stringBufferResult.toString().getBytes("Cp1252"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return newStr;
    }
}
