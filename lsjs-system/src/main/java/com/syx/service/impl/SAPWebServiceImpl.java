package com.syx.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 宋远欣
 * @date 2022/5/19
 **/
public class SAPWebServiceImpl {

    public void getPDKKandRZLL(){
        String postUrl = "http://s4hanadev.jzj.cn:8002/sap/bc/srt/rfc/sap/zhr_interface_rfc008/300/zhr_interface_rfc008/zhr_interface_rfc008";
        String Username = "056925";
        String Password = "056925*JL";
        List<String> pernr = new ArrayList<>();
        pernr.add("000333");
        pernr.add("000447");
        String soapXml = getXML(pernr);
        System.out.println(soapXml);
    }

    private String getXML(List<String> pernr) {
        String soapXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:con=\"http://config.inca.com\" xmlns:xsd=\"http://xsd.config.inca.com/xsd\">\n"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<urn:ZHR_INTERFACE_RFC008>"
                + "<!--Optional:-->"
                + "<INPUT>"
                + "<!--Zero or more repetitions:-->"
                + "<item>"
                + "<pernr>" + pernr + "</pernr>"
                + "</item>"
                + "</INPUT>"
                + "</urn:ZHR_INTERFACE_RFC008>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";
        return soapXml;
    }
}
