package board.common;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

//import board.board.dto.BoardFileDto;
import board.board.entity.BoardFileEntity;

@Component
public class FileUtils {
	
	private static final String filePath = "/Users/owen/toy/board_chain/images/";
	
//	public List<BoardFileEntity> parseFileInfo(MultipartHttpServletRequest multipartHttpServletRequest) throws Exception{
	public static String[] parseFileInfo(MultipartHttpServletRequest multipartHttpServletRequest) throws Exception{
		if(ObjectUtils.isEmpty(multipartHttpServletRequest)){
			return null;
		}
	
	
		List<BoardFileEntity> fileList = new ArrayList<>();
		List<Object> objList = new ArrayList<>();
		
	    String fileSize = "";
	    String originalFileName = "";
	    String storedFilePath = "";
	    String[] fileinfo = null;
		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd"); 
    	ZonedDateTime current = ZonedDateTime.now();
    	String path = filePath + current.format(format);
    	File file = new File(path);
		if(file.exists() == false){
			file.mkdirs();
		}
		
		Iterator<String> iterator = multipartHttpServletRequest.getFileNames();
		
		String newFileName, originalFileExtension, contentType;
		
		while(iterator.hasNext()){
			List<MultipartFile> list = multipartHttpServletRequest.getFiles(iterator.next());
			for (MultipartFile multipartFile : list){
				if(multipartFile.isEmpty() == false){
					contentType = multipartFile.getContentType();
					if(ObjectUtils.isEmpty(contentType)){
						break;
					}
					else{
						 originalFileName = multipartFile.getOriginalFilename();
						if(contentType.contains("image/jpeg")) {
							originalFileExtension = ".jpg";
						}
						else if(contentType.contains("image/png")) {
							originalFileExtension = ".png";
						}
						else if(contentType.contains("image/gif")) {
							originalFileExtension = ".gif";
						}
						else{
							break;
						}
					}
					
					//newFileName = Long.toString(System.nanoTime()) + originalFileExtension;
					newFileName =  originalFileName;
					BoardFileEntity boardFile = new BoardFileEntity();
					boardFile.setFileSize(multipartFile.getSize());
					boardFile.setOriginalFileName(multipartFile.getOriginalFilename());
					boardFile.setStoredFilePath(path + "/" + newFileName);
					fileList.add(boardFile);
					
					file = new File(path + "/" + newFileName);
					multipartFile.transferTo(file);
					
					//컨트랙트를 위한 부분 별개 저장
					
//					originalFileName = multipartFile.getOriginalFilename(); // 원본 파일 명
//					fileSize =  Integer.toString((int)multipartFile.getSize()); // 파일 사이즈
//					storedFilePath = path + "/" + newFileName;
//			        //path를 도메인 패스로 잡아서 올리즈아~ 
//			        
//			        objList.add(originalFileName);			        
//			        objList.add(storedFilePath);
//			        objList.add(fileSize);
			        
				}
			}
			fileinfo = objList.toArray(new String[objList.size()]);
		}
		//return fileList;
		return fileinfo;
	}
}