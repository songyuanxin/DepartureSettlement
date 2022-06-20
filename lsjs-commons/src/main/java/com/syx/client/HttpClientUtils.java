package com.syx.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 宋远欣
 * @date 2022/5/24
 **/
public class HttpClientUtils {
    static int socketTimeout = 30000;// 请求超时时间
    static int connectTimeout = 30000;// 传输超时时间

    /**
     * 使用SOAP1.1发送消息
     * @param postUrl
     * @param username
     * @param password
     * @return
     */
    public static String postSoapOne(String soapXml,String postUrl, String username, String password) {
        int timeOut = 50000;
        HttpClient client = new HttpClient();
        //需要用户名和密码验证
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
        client.getState().setCredentials(AuthScope.ANY, creds);
        //webservice地址
        PostMethod postMethod = new PostMethod(postUrl);
        //设置连接超时
        client.getHttpConnectionManager().getParams().setConnectionTimeout(timeOut);
        //设置读取时间超时
        client.getHttpConnectionManager().getParams().setSoTimeout(timeOut);
        String result = "";

        try {
            //把Soap请求数据添加到PostMethod中
            RequestEntity requestEntity = new StringRequestEntity(soapXml,"text/xml", "UTF-8");
            //设置请求头部
//            postMethod.setRequestHeader("SOAPAction", "");
            postMethod.addRequestHeader(username, password);
            //设置请求体
            postMethod.setRequestEntity(requestEntity);
            int status = client.executeMethod(postMethod);
            if (status == 200){
                InputStream is = postMethod.getResponseBodyAsStream();
                result = IOUtils.toString(is, StandardCharsets.UTF_8);
                System.out.println("请求成功！"+"\n"+"返回结果:"+result);
            }else {
//                System.out.println("请求失败！"+"\n"+"错误代码："+status+"\n"+"返回报文:"+"\n"+postMethod.getResponseBodyAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Document strXmlToDocument(String parseStrXml){
        StringReader read = new StringReader(parseStrXml);
        //创建新的输入源SAX解析器将使用InputSource 对象来确定如何读取XML输入
        InputSource source = new InputSource(read);
        //创建一个新的SAXBuilder
        //新建立构造器
        SAXBuilder sb = new SAXBuilder();
        Document doc = null;
        try {
            doc = sb.build(source);
        }catch (JDOMException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return doc;
    }

    public static String getValueByElementName(Document doc, String finalNodeName){
        Element root = doc.getRootElement();
        HashMap<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> resultMap = getChildAllText(doc, root, map);
        String result = (String) resultMap.get(finalNodeName);
        return result;
    }

    private static Map<String, Object> getChildAllText(Document doc, Element e, HashMap<String, Object> map) {
        if (e != null){
            //如果存在子节点
            if (e.getChildren() != null){
                List<Element> list = e.getChildren();
                //循环输出
                for (Element el:list){
                    if (el.getChildren().size() > 0){
                        getChildAllText(doc, el, map);
                    }else{
                        //将叶子节点值压入map
                        map.put(el.getName(), el.getTextTrim());
                    }
                }
            }
        }
        return map;
    }
}
