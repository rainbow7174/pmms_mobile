package kr.hs2.co.vo;

public class PhotoVO {
	public PhotoVO() {
		_idx = 0;
		_title = "";
		_thumbnail = "";
		_fileinfo = "";
		_takedate = "";
		_imgsize = "";
		_filesize = "";
		_memo = "";
		_oImgUrl = "";
		_filename= "";
		_totalcnt = 0;
		_pagescale = 0;
		_regdate = "";
		_imgwidth = 0;
		_imgheight = 0;
		_gallery = "";
	}
	
	public int getIdx() { return _idx; }
	public String getTitle() { return _title; }
	public String getThumbnail() { return _thumbnail; }
	public String getFileinfo() { return _fileinfo; }
	public String getTakedate() { return _takedate; }
	public String getImgsize() { return _imgsize; }
	public String getFilesize() { return _filesize; }
	public String getMemo() { return _memo; }
	public String getOImgUrl() { return _oImgUrl; }
	public String getFilename() { return _filename; }
	public int getTotalcnt() { return _totalcnt; }
	public int getPagescale() { return _pagescale; }
	public String getRegdate() { return _regdate; }
	public int getImgwidth() { return _imgwidth; }
	public int getImgheight() { return _imgheight; }
	public String getGallery() { return _gallery; }
	
	public void setIdx(int idx) { _idx = idx; }
	public void setTitle(String title) { _title = title; }
	public void setThumbnail(String thumbnail) { _thumbnail = thumbnail; }
	public void setFileinfo(String fileinfo) { _fileinfo = fileinfo; }
	public void setTakedate(String takedate) { _takedate = takedate; }
	public void setImgsize(String imgsize) { _imgsize = imgsize; }
	public void setFilesize(String filesize) { _filesize = filesize; }
	public void setMemo(String memo) { _memo = memo; }
	public void setOImgUrl(String oImgUrl) { _oImgUrl = oImgUrl; }
	public void setFilename(String filename) { _filename = filename; }
	public void setTotalcnt(int totalcnt) { _totalcnt = totalcnt; }
	public void setPagescale(int pagescale) { _pagescale = pagescale; }
	public void setRegdate(String regdate) { _regdate = regdate; }
	public void setImgwidth(int imgwidth) { _imgwidth = imgwidth; }
	public void setImgheight(int imgheight) { _imgheight = imgheight; }
	public void setGallery(String gallery) { _gallery = gallery; }
	
	private int _idx; // 일련번호
	private String _title; // 제목
	private String _thumbnail; // 썸네일 이미지 URL 주소
	private String _fileinfo; // 이미지 기본 정보
	private String _takedate; // 촬영일자
	private String _imgsize; // 이미지크기
	private String _filesize; // 파일용량
	private String _memo; // 설명
	private String _oImgUrl; // 원본이미지 URL
	private String _filename; // 파일명
	private int _totalcnt;
	private int _pagescale;
	private String _regdate; // 등록일자
	private int _imgwidth;
	private int _imgheight;
	private String _gallery; // 갤러리 원본이미지 URL
}

