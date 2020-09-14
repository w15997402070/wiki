# univocity简介

univocity 解析csv文件,执行速度快也可以解析tsv文件

maven引入包

```xml
   <dependency>
      <groupId>com.univocity</groupId>
      <artifactId>univocity-parsers</artifactId>
      <version>2.8.4</version>
    </dependency>
```

java代码

```java
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CsvUtil {
    public void parseCsv(){
        String fileName = "D:\\tmp\\20191225.csv";
        //1. 创建配置对象
        CsvParserSettings csvParserSettings = new CsvParserSettings();
        //打开解析器的自动检测功能,自动检测输入中包含何种行分隔符
        csvParserSettings.setLineSeparatorDetectionEnabled(true);
        //2. 创建RowListProcess对象,用来把每个解析的行存储在列表中
        RowListProcessor rowListProcessor = new RowListProcessor();
        //配置解析器
        csvParserSettings.setRowProcessor(rowListProcessor);
        //如果待解析的csv文件包含标题头,就可以把第一个解析行看做文件中每个列的标题
        csvParserSettings.setHeaderExtractionEnabled(true);
        //3. 使用配置设置,创建解析对象
        CsvParser csvParser = new CsvParser(csvParserSettings);
        //解析文件
        csvParser.parse(new File(fileName));

        //如果有标题,可以用下面代码获取标题
        String[] headers = rowListProcessor.getHeaders();
        //列表中找到行值
        List<String[]> rows = rowListProcessor.getRows();
        for (int i = 0; i < rows.size(); i++) {
           log.info(Arrays.asList(rows.get(i)).toString());
        }
    }
     /**
     * 解析TSV文件
     */
    public void parseTsv(){
        String fileName = "D:\\tmp\\testTsv.tsv";
        //1. 创建配置对象
        TsvParserSettings tsvParserSettings = new TsvParserSettings();
        //打开解析器的自动检测功能,自动检测输入中包含何种行分隔符
        tsvParserSettings.getFormat().setLineSeparator("n");
        //3. 使用配置设置,创建解析对象
        TsvParser tsvParser = new TsvParser(tsvParserSettings);
        //列表中找到行值
        List<String[]> rows = tsvParser.parseAll(new File(fileName));
        for (int i = 0; i < rows.size(); i++) {
            log.info(Arrays.asList(rows.get(i)).toString());
        }
    }

    public static void main (String [] args){
        new CsvUtil().parseCsv();
    }
}

```

