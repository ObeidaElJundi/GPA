package com.coding4fun.models;

public class Course{

	private String name;
	private String grade;
	private int credit;
	private int creditIndex, gradeIndex;
	private static final String[] gradesByLetter = {"A+","A","A-","B+","B","B-","C+","C","C-","D","D-","F"};
	private static final double[] gradesByNumber = {4.0,4.0,3.667,3.333,3.0,2.667,2.333,2.0,1.667,1.333,1.0,0.0};
	private static final int[] credits = {1,2,3,4,5};
	
	public Course() {
		name = "";
		creditIndex = 2;
		gradeIndex = 2;
		grade = gradesByLetter[gradeIndex];
		credit = credits[creditIndex];
	}
	
	public void updateGrade(int i){
		gradeIndex = i;
		grade = gradesByLetter[i];
	}
	
	public void updatecredit(int i){
		creditIndex = i;
		credit = credits[i];
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(int index) {
		this.grade = gradesByLetter[index];
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int index) {
		this.credit = credits[index];
	}

	public int getCreditIndex() {
		return creditIndex;
	}

	public int getGradeIndex() {
		return gradeIndex;
	}

	public double gradeByNumberFromLetter(String g){
		for(int i=0;i<gradesByLetter.length;i++){
			if(g.equals(gradesByLetter[i]))
				return gradesByNumber[i];
		}
		return 0.0;
	}
	
}
