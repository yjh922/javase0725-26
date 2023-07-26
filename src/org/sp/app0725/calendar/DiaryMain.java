package org.sp.app0725.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import util.StringManager;

//다이어리 만들기
public class DiaryMain extends JFrame{
	JPanel p_north;
	JPanel p_west;
	JPanel p_center;
	JPanel p_east;
	JButton bt_prev;
	JButton bt_next;
	JLabel la_title;
	String[] dayTitle= {"Sun","Mon","Tue","Wed","Thur","Fri","Sat"};
	
	Calendar cal;//이전, 다음 버튼등에 의해 조작될 날짜 객체
	int currentYear;//현재 사용자가 보게될 연
	int currentMonth;//현재 사용자가 보게될 월
	
	//탭메뉴 처리
	TabMenu[] tab;
	Thread[] thread;//탭메뉴의 애니메이션을 구현할 쓰레드
	Color[] tabColor= {Color.RED, Color.YELLOW, Color.GREEN, Color.PINK};
	
	//날짜 셀
	NumCell[][] numCells=new NumCell[6][7];
	//팝업창
	Popup popup;
	
	
	public DiaryMain() {
		//UI 생성하기
		p_north = new JPanel();
		p_west = new JPanel();
		p_center = new JPanel();
		p_east = new JPanel();
		bt_prev = new JButton("◀");
		bt_next = new JButton("▶");
		la_title = new JLabel("1987-11-01");
		
		cal = Calendar.getInstance();//날짜 객체 생성(디폴트=현재날짜)
		//날짜 객체로 부터 연, 월 구하기
		currentYear=cal.get(Calendar.YEAR);
		currentMonth=cal.get(Calendar.MONTH);
		
		//탭메뉴 처리
		tab = new TabMenu[4];
		thread = new Thread[4];
		

		
		for(int i=0; i<tab.length;i++) {
			tab[i] =new TabMenu(tabColor[i], -95, 30+(i*71), 100, 70);
			thread[i] = new Thread(tab[i]);
			//thread[i].start();
			p_west.add(tab[i]);
		}

		
		//스타일
		la_title.setFont(new Font("Arial Black", Font.BOLD, 45));
		
		p_west.setPreferredSize(new Dimension(150,750));
		p_center.setPreferredSize(new Dimension(800,750));
		p_east.setPreferredSize(new Dimension(150,750));
		
		Border border = new LineBorder(Color.LIGHT_GRAY,1,true);
		
		p_west.setBorder(new TitledBorder(border,"Registration"));
		p_center.setBorder(new TitledBorder(border,"Content"));
		p_east.setBorder(new TitledBorder(border,"Detail"));
		
		//조립
		p_north.add(bt_prev);
		p_north.add(la_title);
		p_north.add(bt_next);
		
		//setBounds로 좌표에 의한 위치를 설정할 경우 컨테이너엔 레이아웃 즉 배치관리자가
		//적용되지 말아야 한다.
		p_west.setLayout(null);
		
		
		add(p_north, BorderLayout.NORTH);
		add(p_west, BorderLayout.WEST);
		add(p_center);
		add(p_east, BorderLayout.EAST);
		
		createCell();//달력에 사용될 셀 생성하기
		printTitle();//달력 제목 출력
		printNum();//날짜 출력
		
		setSize(1100,850);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		//팝업창 생성하기
		popup = new Popup();
		
		//버튼과 리스너 연결
		//내부익명클래스 마저도 더욱 줄여서 코드 작성하고 싶다면??
		//객체차원까지 사용할 필요가 있는가? 간단한 함수기반으로 작성하는 방법은 없는가?
		//람다표현식(Lambda) - 함수 기반 언어에서 유래
		//즉 거창하게 객체까지 필요없는 단순한 코드인경우 함수 수준으로 처리함이 더 코드량이 적음
		bt_prev.addActionListener((e)->{
			//System.out.println(e);
			prev();
		});
		bt_next.addActionListener((e)->{
			//System.out.println(e);
			next();
		});
	}
	
	
	
	//셀 만들기
	public void createCell() {
		//요일셀 만들기
		for(int i=0;i<dayTitle.length;i++) {
			DayCell cell = new DayCell(Color.DARK_GRAY, 100, 45);
			cell.setTitle(dayTitle[i]);
			p_center.add(cell);
		}
		
		//날짜 셀 만들기
		for(int a=0;a<6;a++) {//6층
			for(int i=0; i<7;i++) {//7호수
				NumCell cell=new NumCell(this,Color.WHITE, 100, 100);
				cell.setTitle("0");
				//한 층에 소속된 호수들을 배열에 채우기
				numCells[a][i]=cell;
				p_center.add(cell);
			}
		}
	}
	
	//날짜 제목 출력
	public void printTitle() {
		int year=cal.get(Calendar.YEAR);
		int mm=cal.get(Calendar.MONTH);
		
		//출력시 2자리수 미만인 경우 즉 1자리수 인 경우 숫자앞에 0을 붙이자
		//예) 7인 경우 --> 07로 표현
		
		
		la_title.setText(year+"-"+StringManager.getNumString(mm+1));
	}
	
	//이전 날짜 처리
	public void prev() {
		//이전 월 처리
		int mm=cal.get(Calendar.MONTH);
		cal.set(Calendar.MONTH, mm-1);//조작
		printTitle();//제목출력
		printNum();//날짜 출력
	}
	
	//다음 날짜 처리
	public void next() {
		//다음 월 처리
		int mm=cal.get(Calendar.MONTH);
		cal.set(Calendar.MONTH, mm+1);//조작
		printTitle();//제목출력
		printNum();//날짜 출력
		//기존 셀에 들었는 아이콘 삭제
		
		
	}
	
	//해당 월의 시작 요일 구하기
	public int getStartDayOfWeek() {
		//날짜 객체 하나를 해당월의 1일로 조작해서 그 날이 무슨 요일인지 구하자
		Calendar c = Calendar.getInstance();//조작용 객체(망가져도 상관없음)
		int yy=cal.get(Calendar.YEAR);//현재 보고 있는 연도
		int mm=cal.get(Calendar.MONTH);//현재 보고 있는 월
		
		c.set(yy, mm, 1);//날짜 객체를 1일로 조작
		
		int day=c.get(Calendar.DAY_OF_WEEK);//1일의 요일 구하기
		
		System.out.println(day);
		return day;//요일 반환
	}
	
	//해당 월이 몇일까지 있는지
	public int getLastDateOfMonth() {
		//현재 보고 있는 월의 다음월 및 0일로 조작을 가한 후 몇일인지 물어보자
		
		int yy=cal.get(Calendar.YEAR);
		int mm = cal.get(Calendar.MONTH);
		
		//조작을 가해보자
		Calendar c= Calendar.getInstance();
		c.set(yy, mm+1, 0);
		int dd=c.get(Calendar.DATE);
		return dd;
	}
	
	//날짜 숫자 출력처리
	public void printNum() {
		//기존의 셀에 출력된 숫자 모두 지우기
		for(int a=0;a<numCells.length;a++) {
			for(int i=0; i<numCells[a].length;i++) {
				numCells[a][i].setTitle("");
				
				//아이콘 삭제하기
				numCells[a][i].iconBox.removeAll();
			}
		}
		
		
		
		int startDay=getStartDayOfWeek();//해당 월이 무슨 요일부터 시작하는지 그 값을 얻기
		int lastDate=getLastDateOfMonth();//해당 월이 몇일까지 있는지 그 값을 얻기
		System.out.println(lastDate+"까지에요");
		
		//각셀에 알맞는 숫자 채우기
		int count=0;//셀의 순번을 체크하기 위한 변수
		int num=0;//실제 날짜를 담당할 변수
		
		for(int a=0;a<numCells.length;a++) {
			for(int i=0;i<numCells[a].length;i++) {
				count++;
				if(count>=startDay && num<lastDate) {
					num++;
					numCells[a][i].setTitle(Integer.toString(num));
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new DiaryMain();
	}
}








