package kr.hs2.co.vo;

public class MovieVO {
	public MovieVO() {
		_idx = 0;
		_title = "";
		_imgfile = "";
		_vodfile = "";
		_fileinfo = "";
		_takedate = "";
		_imgsize = "";
		_filesize = "";
		_memo = "";
		_filename= "";
		_regdate = "";
		_totalcnt = 0;
		_pagescale = 0;
		_imgwidth = 0;
		_imgheight = 0;
	}
	
	public int getIdx() { return _idx; }
	public String getTitle() { return _title; }
	public String getImgfile() { return _imgfile; }
	public String getVodfile() { return _vodfile; }
	public String getFileinfo() { return _fileinfo; }
	public String getTakedate() { return _takedate; }
	public String getImgsize() { return _imgsize; }
	public String getFilesize() { return _filesize; }
	public String getMemo() { return _memo; }
	public String getFilename() { return _filename; }
	public String getRegdate() { return _regdate; }
	public int getTotalcnt() { return _totalcnt; }
	public int getPagescale() { return _pagescale; }
	public int getImgwidth() { return _imgwidth; }
	public int getImgheight() { return _imgheight; }
	
	public void setIdx(int idx) { _idx = idx; }
	public void setTitle(String title) { _title = title; }
	public void setImgfile(String imgfile) { _imgfile = imgfile; }
	public void setVodfile(String vodfile) { _vodfile = vodfile; }
	public void setFileinfo(String fileinfo) { _fileinfo = fileinfo; }
	public void setTakedate(String takedate) { _takedate = takedate; }
	public void setImgsize(String imgsize) { _imgsize = imgsize; }
	public void setFilesize(String filesize) { _filesize = filesize; }
	public void setMemo(String memo) { _memo = memo; }
	public void setFilename(String filename) { _filename = filename; }
	public void setRegdate(String regdate) { _regdate = regdate; }
	public void setTotalcnt(int totalcnt) { _totalcnt = totalcnt; }
	public void setPagescale(int pagescale) { _pagescale = pagescale; }
	public void setImgwidth(int imgwidth) { _imgwidth = imgwidth; }
	public void setImgheight(int imgheight) { _imgheight = imgheight; }
	
	private int _idx; // �Ϸù�ȣ
	private String _title; // ����
	private String _imgfile; // �̸����� �̹���
	private String _vodfile; // VOD ����
	private String _fileinfo; // �̹��� �⺻ ����
	private String _takedate; // �Կ�����
	private String _imgsize; // �̹���ũ��
	private String _filesize; // ���Ͽ뷮
	private String _memo; // ����
	private String _filename; // ���ϸ�
	private String _regdate; // �������
	private int _totalcnt;
	private int _pagescale;
	private int _imgwidth;
	private int _imgheight;
}
