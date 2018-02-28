/**
 * 
 */
package site.franksite.cet.exe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import site.franksite.cet.http.CETHttpBusiness;
import site.franksite.cet.http.CETHttpParser;
import site.franksite.cet.http.bean.CETStudentBean;

/**
 * 主程序入口
 * @author Frank
 *
 */
public class MainEntry {

	private static final String CONSOLE_HEADER = "姓名\t准考证号\t总成绩\t听力\t阅读\t写作/翻译";
	private static final Logger LOG = Logger.getLogger(MainEntry.class);
	private static Scanner scanner = new Scanner(System.in);
	
	private static CommandLine parseCommandLine(String []args) throws ParseException {
		Options options = new Options();
		options.addOption("h", false, "Review the usage of the tool.");
		options.addOption("p", false, "Query CET grade for personal mode.");
		options.addOption("n", true, "The name for personal mode.");
		options.addOption("x", true, "The examination number for personal mode.");
		options.addOption("c", false, "Show result with console text.");
		options.addOption("o", true, "The target for the result to output.");
		options.addOption("b", false, "Query CET grade for batch mode.");
		options.addOption("i", true, "The input file for batch mode filled with names and examination numbers.");
		options.addOption("e", false, "The output file as Excel.");
		
		CommandLineParser parser = new BasicParser();

		return parser.parse(options, args);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CommandLine param = parseCommandLine(args);
			if (param.hasOption("h")) {
				printHelpDoc();
			} else if (param.hasOption("p")) {
				// 个人查询模式
				// 必要参数，-n,-x
				if (!param.hasOption("n") || !param.hasOption("x")) {
					System.out.println("个人查询模式下，必须指定参数-n及参数-x，详细用法请使用-h查看帮助");
					return;
				}
				String name = param.getOptionValue("n");
				String code = param.getOptionValue("x");
				CETStudentBean stuBean = new CETStudentBean();
				stuBean.setName(name);
				stuBean.setExamNumber(code);
				CETHttpBusiness business = new CETHttpBusiness();
				
				JSONObject jsonResult;
				boolean inputed = false;
				do {
					File imgFile = business.getImageCode(code);
					String imgCode = getCodeFromInput(imgFile, inputed);
					inputed = true;
					stuBean.setImgCode(imgCode);
					String result = business.getGrade(stuBean);
					// 尝试解析JSON
					jsonResult = new CETHttpParser().parseResult(result);
					if (imgFile.exists()) {
						imgFile.delete();
					}
					if (jsonResult.containsKey("error")) {
						System.out.println("查询失败：" + jsonResult.get("error"));
					} else {
						// 输出结果
						printPersonalMode(param, jsonResult);
					}
				} while (jsonResult.containsKey("error"));
			} else if (param.hasOption("b")) {
				// 批量查询模式
				// 必须含有i参数
				if (!param.hasOption("i")) {
					System.out.println("批量查询模式下，必须含有参数i，并指定输入文件，详细使用请查看帮助");
					return;
				} else {
					String inputFilePath = param.getOptionValue("i");
					File inputFile = new File(inputFilePath);
					if (!inputFile.exists()) {
						System.out.println("文件：" + inputFilePath + "不存在，无法读取！");
						return;
					}
					try {
						String inputString = FileUtils.readFileToString(inputFile, "UTF-8");
						String[] allLines = inputString.split("\\r?\\n");
						
						Queue<CETStudentBean> studentQueue = new LinkedBlockingQueue<CETStudentBean>();
						for (String line : allLines) {
							line = line.replace((char)65279 + "", "");
							CETStudentBean stu = new CETStudentBean();
							StringTokenizer spliter = new StringTokenizer(line);
							String name = spliter.nextToken().trim();
							if (3 < name.length()) {
								LOG.debug((int)name.charAt(0));
								stu.setName(name.substring(0, 3));
							} else {
								stu.setName(name);
							}
							stu.setExamNumber(spliter.nextToken());
							studentQueue.offer(stu);
						}
						JSONArray arr = new JSONArray();
						while (!studentQueue.isEmpty()) {
							CETHttpBusiness business = new CETHttpBusiness();
							CETStudentBean stu = studentQueue.element();
							JSONObject result;
							boolean inputed = false;
							do {
								File codeFile = business.getImageCode(stu.getExamNumber());
								String imgCode = getCodeFromInput(codeFile, inputed);
								inputed = true;
								stu.setImgCode(imgCode);
								String grade = business.getGrade(stu);
								
								result = new CETHttpParser().parseResult(grade);
								if (codeFile.exists()) {
									codeFile.delete();
								}
								if (result.containsKey("error")) {
									System.out.println("查询失败：" + result.get("error"));
								}
							} while (result.containsKey("error"));
							studentQueue.poll();
							arr.add(result);
						}
						printBatch(param, arr);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						
					}
				}
			}
		} catch (ParseException e) {
			LOG.trace(e);
		} finally {
			scanner.close();
		}
	}
	private static void printHelpDoc() {
		System.out.println("该工具用于快速查询你的四六级成绩，支持个人、批量查询。个人查询通过使用姓名、准考证号查询。批量查询支持从输入源中获取姓名、准考证。");
		System.out.println("工具用法介绍：");
		System.out.println("\t[-p]\t采用个人模式查询；");
		System.out.println("\t[-x 准考证号]\t指明个人模式查询下的准考证号；");
		System.out.println("\t[-n 姓名]\t指明个人查询模式下的姓名；");
		System.out.println("\t[-c]\t使用控制台输出查询结果（默认输出设备）；");
		System.out.println("\t[-o 文件路径]\t结果输出目录；");
		System.out.println("\t[-b]\t采用批量查询模式查询；");
		System.out.println("\t[-i 文件路径]\t指明批量模式下文件输出目录；");
		System.out.println("\t[-e]\t指明批量模式下文件输出类型是否为excel；");
		System.out.println("完整实例：");
		System.out.println("\tjava -jar cet-grade.jar -p -n 张三 -x 320256236486584 [-o C:\\cetgrade] 或 cet-grade.jar -p -n 张三 -x 320256236486584 [-c]");
		System.out.println("\tjava -jar cet-grade.jar -b -i C:\\input.txt [-o C:\\cetgrade] 或cet-grade.jar -b -i C:\\input.txt [-c]");
	}
	
	private static String getCodeFromInput(File codeFile, boolean inputed) {
		if (codeFile.exists()) {
			if (!inputed) {
				System.out.println("请输入验证码，图片位于" + codeFile.getAbsolutePath() + ":");
			} else {
				System.out.println("请重新输入验证码，图片位于" + codeFile.getAbsolutePath() + ":");
			}
		} else {
			System.out.println("验证码获取失败，已提前退出！");
			return null;
		}
		String imgCode = scanner.nextLine();
		return imgCode.toLowerCase();
	}
	
	private static String constructRowText(JSONObject gradeJson) {
		return gradeJson.getString("n") + "\t" + 
				gradeJson.getString("z") + "\t" + 
				gradeJson.getDouble("s") + "\t" +
				gradeJson.getDouble("l") + "\t" + 
				gradeJson.getDouble("r") + "\t" + 
				gradeJson.getDouble("w");
	}
	
	private static void printPersonalMode(CommandLine commandLine, JSONObject gradeJson) {
		if (commandLine.hasOption("c") || !commandLine.hasOption("o")) {
			System.out.println(CONSOLE_HEADER);
			System.out.println(constructRowText(gradeJson));
		} else if (commandLine.hasOption("o")) {
			// 输出文件目录
			String outputPath = commandLine.getOptionValue("o", System.getProperty("user.dir"));
			File outputFile = new File(outputPath);
			if (outputFile.isFile()) {
				outputFile = outputFile.getParentFile();
			}
			try {
				FileUtils.writeStringToFile(new File(outputFile, "cetgrade.txt"), CONSOLE_HEADER + System.getProperty("line.separator") + constructRowText(gradeJson), "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void printBatch(CommandLine commandLine, JSONArray jsonArr) {
		if (commandLine.hasOption("c") || !commandLine.hasOption("o")) {
			System.out.println(CONSOLE_HEADER);
			for (Object jsonObj : jsonArr) {
				System.out.println(constructRowText(((JSONObject) jsonObj)));
			}
		} else if (commandLine.hasOption("o")) {
			// 输出文件目录
			String outputPath = commandLine.getOptionValue("o", System.getProperty("user.dir"));
			File outputFile = new File(outputPath);
			if (outputFile.isFile()) {
				outputFile = outputFile.getParentFile();
			}
			if (!commandLine.hasOption("e")) {
				try {
					StringBuffer buffer = new StringBuffer();
					buffer.append(CONSOLE_HEADER + System.getProperty("line.separator"));
					for (Object jsonObj : jsonArr) {
						buffer.append(constructRowText(((JSONObject) jsonObj)) + System.getProperty("line.separator"));
					}
					FileUtils.writeStringToFile(new File(outputFile, "cetgrade.txt"), buffer.toString(), "UTF-8");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					
				}
			} else {
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(new File(outputPath, "cetgrade.xls"));
					outputToExcel(jsonArr, output);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private static void outputToExcel(JSONArray arr, OutputStream output) {
		HSSFWorkbook cetGraeBook = new HSSFWorkbook();
		HSSFSheet cetGradeSheet = cetGraeBook.createSheet("CET成绩");
		// 创建头
		HSSFRow header = cetGradeSheet.createRow(0);
		HSSFCell cell = header.createCell(0);
		cell.setCellValue("姓名");
		
		cell = header.createCell(1);
		cell.setCellValue("准考证号");
		
		cell = header.createCell(2);
		cell.setCellValue("学校");
		
		cell = header.createCell(3);
		cell.setCellValue("总成绩");
		
		cell = header.createCell(4);
		cell.setCellValue("听力");
		
		cell = header.createCell(5);
		cell.setCellValue("阅读");
		
		cell = header.createCell(6);
		cell.setCellValue("写作/翻译");
		
		int rowIndex = 1;
		for (Object json : arr) {
			HSSFRow row = cetGradeSheet.createRow(rowIndex);
			HSSFCell nameCell = row.createCell(0);
			nameCell.setCellValue(((JSONObject) json).getString("n"));

			HSSFCell numberCell = row.createCell(1);
			numberCell.setCellValue(((JSONObject) json).getString("z"));
			
			HSSFCell schoolCell = row.createCell(2);
			schoolCell.setCellValue(((JSONObject) json).getString("x"));
			
			HSSFCell gradeCell = row.createCell(3);
			gradeCell.setCellValue(((JSONObject) json).getDouble("s"));
			
			HSSFCell listenCell = row.createCell(4);
			listenCell.setCellValue(((JSONObject) json).getDouble("l"));
			
			HSSFCell readCell = row.createCell(5);
			readCell.setCellValue(((JSONObject) json).getDouble("r"));
			
			HSSFCell writeCell = row.createCell(6);
			writeCell.setCellValue(((JSONObject) json).getDouble("w"));
			rowIndex++;
		}
		
		try {
			cetGraeBook.write(output);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				cetGraeBook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
