# cet-grade

## 工具名

CET Grade Querying Tool.CET四六级成绩查询工具。

## 描述信息

支持四六级成绩查询，采用命令行方式，通过姓名、准考证号并借助爬虫工具，获取四六级成绩（目前已支持2017下半年四六级成绩查询）。

成绩查询支持个人查询、批量查询。

* 个人查询

个人查询需要提供姓名、准考证号，可查询四六级总成绩、听力、阅读、写作等成绩。

* 批量查询

批量查询目前仅支持从源文件读入考生信息，考生信息包括：姓名、准考证号，可查询四六级总成绩、听力、阅读、写作等成绩。

四六级成绩支持直接控制台结果输出，文件输出。当使用个人模式查询时，文件输出将以文本文档形式输出，并可由用户自定义输出路径。当使用批量查询模式时，文件输出可选用文本文档及excel 2003兼容性工作表形式输出。

## 文档

*本工具采用Java 1.5版本进行编译及打包，默认情况下支持Jdk 1.5及以上版本，若需要在shell(Unix或Linux平台)及CMD(Windows平台)中使用该文档，请预先配置好Java运行环境*

本文档中所编译jar包采用maven默认打包名称：cet-grade-0.01.jar，若用户在实际使用过程中，对jar包名称进行了修改，请使用修改后的名称进行操作。

* 查看工具帮助

`java -jar cet-grade-0.01.jar -h`

* 个人查询

1. 简单查询

`java -jar cet-grade-0.01.jar -p -n [姓名] -x [准考证号]`

其中`-p -n -x`为必要参数，其用于指定当前查询模式为个人查询模式，并指定要查询的考生姓名及准考证。采用该模式进行成绩查询，工具将采用终端(控制台)作为默认输出设备进行结果输出和显示，并将按照表格形式进行展。

2. 指定输出设备为终端（控制台）

`java -jar cet-grade-0.01.jar -p -c -n [姓名] -x [准考证号]`

其中`-c`为可选参数，其用于指定当前查询模式下，将选用终端(控制台)作为输出设备进行结果显示（缺省情况下，工具将自行选用终端(控制台)进行输出）。

3. 指定输出路径查询

`java -jar cet-grade-0.01.jar -p -c -n [姓名] -x [准考证号] -o [output]`

其中`-o [output]`为可选参数，该参数用于在当期模式下，将查询结果输出至output所指定的目录中。

*注：若当前output所指定的目录为一个文件，工具将自动选择其父目录进行输出。*

在该模式下，查询结果将会输出至：`[output]/cetgrade.txt`文档中。

* 批量查询

1. 简单查询

`java -jar cet-grade-0.01.jar -b -i [input]`

在当前模式下，工具将采用批量查询模式，并使用缺省终端(控制台)作为输出设备。其中参数`-b -i`为必须参数，`-b`用于指定批量查询模式，`-i`用于指定输入文件。

*注：`[input]`所指的输入文件，必须按照一定格式进行编辑，编辑格式为，每行表示一个学生信息，每行包含两个数据，分别为姓名及准考证号，姓名及准考证号使用空格或tab键分隔*

2. 指定输出设备查询

`java -jar cet-grade-0.01.jar -b -i [input] -c`

其中`-c`为可选参数，用于指定输出设备为终端(控制台，缺省设备)，使用该命令，系统将使用默认设备进行输出。

该条件下，`-c`可根据需要替换为`-o [output]`方式进行输出，使用该模式，系统将以文本文档的形式，将结果输出至`[output]/cetgrade.txt`文件中。

3. 指定输出模式查询

`java -jar cet-grade-0.01.jar -b -i -e [input] -o [output]`

该模式下，指定输出文件类型为excel工作簿，并且参数`-o [output]`为必选参数，采用该方式进行查询，工具将会输出查询结果至`[output]/ceggrade.xls`文件中。

## 自行构建

该工具为开源工具，并可植入到你自身的项目中，如作为系统模块为系统提供四六级成绩查询服务。可自行构建RESTful或其他WebServices提供HTTP服务，并以该工具为辅助实现四六级成绩获取功能。亦可根据自身需求，构建可视化程序实现四六级成绩查询。

使用方式大致如下：

* 个人查询

```java
CETHttpBusiness business = new CETHttpBusiness(); // 构建CET查询业务类
CETStudentBean stu = new CETStudentBean(); // 构建请求实体
stu.setName("XXX"); // 设置查询考生姓名
stu.setExamNumber("XXXXXXXXXXXXXXX"); // 设置考生准考证号
File codeFile = business.getImageCode(stu.getExamNumber()); // 请求验证码，并像服务器缓存，方法将返回验证码图片文件，开发者可采用对应方式像用户展示
// 传入验证码
/*该行代码请根据实际需求由用户传入*/
// String code = 
stu.setImgCode(code);
String grade = business.getGrade(stu);// 获取成绩信息
JSONObject result = new CETHttpParser().parseResult(grade); // 解析获取结果
if (result.containsKey("error")) { // 获取结果发生错误
    String errTip = result.getString("error");
} else {
    // 获取成功，result将表示一个JSON对象，其中包含键为：n(姓名),z(准考证号),x(学校),s(笔试总成绩),l(听力),r(阅读),w(写作/翻译),kys(口试成绩),kyz(口试等级)
    result.get("n");
    /*根据实际需要补充代码*/
}
```

* 批量查询

批量查询只需依次查询个人即可。

## 鸣谢

* Apache Http Components
* Junit 
* Jsoup
* Apache Commons I/O
* Log4J
* Apache POI
* Alibaba FastJSON
* Apache Commons Cli