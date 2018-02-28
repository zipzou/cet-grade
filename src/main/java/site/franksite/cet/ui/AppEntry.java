package site.franksite.cet.ui;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

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
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AppEntry {

	private static final Logger LOG = Logger.getLogger(AppEntry.class);
	private JFrame frame;
	private HSSFSheet cetGradeSheet;
	private Queue<CETStudentBean> studentQueue;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppEntry window = new AppEntry();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initData() {
		InputStream studentsFile = AppEntry.class.getResourceAsStream("/students.txt");
		Scanner inputScanner = new Scanner(studentsFile);
		studentQueue = new LinkedBlockingQueue<CETStudentBean>();
		while (inputScanner.hasNext()) {
			String line = inputScanner.nextLine();
			CETStudentBean stu = new CETStudentBean();
			StringTokenizer spliter = new StringTokenizer(line, "\t");
			String name = spliter.nextToken();
			if (3 < name.length()) {
				stu.setName(name.substring(0, 3));
			} else {
				stu.setName(name);
			}
			stu.setExamNumber(spliter.nextToken());
			studentQueue.offer(stu);
		}
		inputScanner.close();
	}
	
	/**
	 * Create the application.
	 */
	public AppEntry() {
		initialize();
		initData();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle("四六级成绩查询");
		frame.setBounds(100, 100, 340, 176);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(new ImageIcon(getClass().getResource("/cet.jpg")).getImage());
		
		JButton btnBatch = new JButton("批量查询开始");
		btnBatch.setBounds(101, 75, 146, 36);
		btnBatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				start();
			}
		});
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(btnBatch);
		
		JButton btnPersonal = new JButton("个人查询");
		btnPersonal.setBounds(101, 10, 146, 36);
		frame.getContentPane().add(btnPersonal);
	}

	public void start() {
		JSONArray arr = new JSONArray();
		while (!studentQueue.isEmpty()) {
			CETHttpBusiness business = new CETHttpBusiness();
			CETStudentBean stu = studentQueue.element();
			JSONObject result;
			do {
				File codeFile = business.getImageCode(stu.getExamNumber());
				Dialog imgDialog = new Dialog(frame, "验证码-" + stu.getName(), true);
				imgDialog.setLayout(null);
				imgDialog.setBounds(100, 100, 300, 300);
				JLabel lblimg = new JLabel();
				lblimg.setBounds(20, 0, 200, 100);
				imgDialog.add(lblimg);
				lblimg.setIcon(new ImageIcon(codeFile.getAbsolutePath()));
				JTextField field = new JTextField();
				field.setBounds(20, 100, 100, 36);
				imgDialog.add(field);
				JButton button = new JButton("确定");
				button.addActionListener(new OkListener(imgDialog));
				button.setBounds(20, 140, 60, 60);
				imgDialog.add(button);
				imgDialog.setVisible(true);
				stu.setImgCode(field.getText());
				String grade = business.getGrade(stu);
				if (stu.getName().equals("陈帆")) {
					LOG.debug(grade);
				}
				result = new CETHttpParser().parseResult(grade);
				if (codeFile.exists()) {
					codeFile.delete();
				}
			} while (result.containsKey("error"));
			studentQueue.poll();
			arr.add(result);
			LOG.debug(result);
		}
		outputToExcel(arr);
	}
	
	public void outputToExcel(JSONArray arr) {
		HSSFWorkbook cetGraeBook = new HSSFWorkbook();
		cetGradeSheet = cetGraeBook.createSheet("CET成绩");
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
			cetGraeBook.write(new File("CET.xlsx"));
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
	
	public class OkListener implements ActionListener {

		private Dialog dialog;
		
		public OkListener(Dialog dialog) {
			super();
			this.dialog = dialog;
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			dialog.setVisible(false);
			dialog.dispose();
		}
		
	}
}
