# 1.��Ŀ���

marddownת������, ��ת��word/ppt/pdf��Ϊmarkdown

�ù�����jar������ʽ�����ṩ��ʽת������

֧�֣�

    1) pdf��ɨ��/��ɨ��)ת��Ϊhtml;
    2��doc/docxת��Ϊhtml;
    3) ppt/pptxת��Ϊhtml;
    4) htmlת��Ϊmd��


# 2.��Ŀ���

����ʹ��fatjar ��ʽ���д�����ɡ�

# 3.��Ŀ����

[root@a3cf Dconverter]# java -jar DConverter.jar -help

```
Usage:  java -jar DConverter.jar <input> [options] <output_path>
        doc/docx:        java -jar DConverter.jar filename.doc/.docx    ./output
        ppt/pptx:        java -jar DConverter.jar filename.ppt/.pptx    ./output
        pdf:             java -jar DConverter.jar filename.pdf ocrflag(0:not need ocr recognition, 1:need ocr recognition)      ./output
        html:            java -jar DConverter.jar filename.html         ./output
        url:             java -jar DConverter.jar http:/https:www.xxx.com       ./output
```

