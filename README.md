# 1.项目简介

marddown转换工具, 可转换word/ppt/pdf等为markdown

该工程以jar包的形式对外提供格式转换服务。

支持：

    1) pdf（扫描/非扫描)转换为html;
    2）doc/docx转换为html;
    3) ppt/pptx转换为html;
    4) html转换为md。


# 2.项目打包

建议使用fatjar 方式进行打包即可。

# 3.项目运行

[root@a3cf Dconverter]# java -jar DConverter.jar -help

```
Usage:  java -jar DConverter.jar <input> [options] <output_path>
        doc/docx:        java -jar DConverter.jar filename.doc/.docx    ./output
        ppt/pptx:        java -jar DConverter.jar filename.ppt/.pptx    ./output
        pdf:             java -jar DConverter.jar filename.pdf ocrflag(0:not need ocr recognition, 1:need ocr recognition)      ./output
        html:            java -jar DConverter.jar filename.html         ./output
        url:             java -jar DConverter.jar http:/https:www.xxx.com       ./output
```

