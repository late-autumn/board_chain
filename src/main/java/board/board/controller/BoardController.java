package board.board.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import board.board.entity.BoardEntity;
import board.board.entity.BoardFileEntity;
import board.board.service.BoardServiceImpl;
import board.common.FileUtils;
import hera.api.model.ContractResult;

@Controller
public class BoardController {

	//@Value("${file.path}")
	//private String filepath;

	protected final Logger logger = getLogger(getClass());

	@Autowired
	private BoardServiceImpl boardService;

	
	@RequestMapping(value = "/blocko/board", method = RequestMethod.GET)
	public ModelAndView openBoardList(HttpServletResponse response, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		response.setContentType("text/html; charset=UTF-8");
		mv.setViewName("board/BoardList");
		ContractResult contractResult = boardService.list();

		if (!"\"empty\"".equals(contractResult.toString())) {
			Gson gson = new Gson();
			Type type = new TypeToken<List<BoardEntity>>() {
			}.getType();

			List<BoardEntity> list = gson.fromJson(contractResult.toString(), type);
			mv.addObject("list", list);
		}
		
		return mv;
	}


	@RequestMapping(value = "/blocko/board/write", method = RequestMethod.GET)
	public String openBoardWrite() throws Exception {
		return "/board/BoardWrite";
	}

	// 게시글 생성
	@RequestMapping(value = "/blocko/board/write", method = RequestMethod.POST)
	public String write(HttpServletResponse response, HttpServletRequest request,
			MultipartHttpServletRequest mtfRequest) throws Exception{
		response.setContentType("text/html; charset=UTF-8");

		// Map paramMap = request.getParameterMap();
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();

		paramMap.put("title", request.getParameterValues("title"));
		paramMap.put("contents", request.getParameterValues("contents"));
		paramMap.put("creatorId", request.getParameterValues("creatorId"));
	 
		List<MultipartFile> files = mtfRequest.getFiles("fileList");
		String[] fileNames = new String[files.size()];
		for (int i=0; i<files.size(); i++) {
			fileNames[i] = files.get(i).getOriginalFilename();
		}
		
 		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd"); 
		ZonedDateTime current = ZonedDateTime.now();
		String path = "images/"+current.format(format);
		String finalPath = path + "/" +Long.toString(System.nanoTime()) + fileNames[0];
		String fileSize = Integer.toString((int)files.get(0).getSize());
		String[] changePath = path.split(" ");
		String[] changeSize = fileSize.split(" ");
		  
		
		paramMap.put("originalFileName", fileNames);
		paramMap.put("storedFilePath", changePath);
		paramMap.put("fileSize",changeSize);
		
  
		int boardIdx = boardService.write(paramMap);
		 if(mtfRequest.getFile("fileList").getSize() != 0) {
		      //boardService.write_item_images(boardIdx, mtfRequest, filepath, request);
			 FileUtils.parseFileInfo(mtfRequest);
		    }
  
		return "redirect:/blocko/board";
	}
	

//	@RequestMapping(value="/blocko/board/{boardIdx}", method=RequestMethod.GET)
//	public ModelAndView openBoardDetail(@PathVariable("boardIdx") int boardIdx) throws Exception{
//		ModelAndView mv = new ModelAndView("/board/blockoBoardDetail");
//		
//		BoardEntity board = boardService.selectBoardDetail(boardIdx);
//		logger.info("변환형태:"+board);
//		mv.addObject("board", board);
//		
//		return mv;
//	}

	@RequestMapping(value = "/blocko/board/{boardIdx}", method = RequestMethod.GET)
	public ModelAndView openBoardDetail(HttpServletResponse response, HttpServletRequest request,
			@PathVariable("boardIdx") int boardIdx) {
		ModelAndView mv = new ModelAndView();
		response.setContentType("text/html; charset=UTF-8");
		mv.setViewName("board/BoardDetail");
		Map paramMap = request.getParameterMap();
		// Optional<BoardEntity> optional = blockoBoardRepository.findById(boardIdx);
		
		boardService.increaseHitCnt(boardIdx);
		ContractResult contractResult = boardService.view(boardIdx);		 
 
		if (!"\"empty\"".equals(contractResult.toString())) {
			Gson gson = new Gson();
			Type type = new TypeToken<List<BoardEntity>>() {
			}.getType();
			 
			List<BoardEntity> board = gson.fromJson(contractResult.toString(), type);
			
			if( board.size()!=0){
			mv.addObject("board", board.get(0));
			}
		}
 
		 //FileDetail
		if (!"\"empty\"".equals(contractResult.toString())) {
	    ContractResult contractResult_item = boardService.view_Image(boardIdx);
	    Gson gson1 = new Gson();
	    Type type1 = new TypeToken<List<BoardFileEntity>>() {
	    }.getType();
	    logger.info("파일 이름"+contractResult_item.toString());
	    List<BoardFileEntity> contactList_item = gson1.fromJson(contractResult_item.toString(), type1);
	    	if(contactList_item.size()!=0) {
	    		mv.addObject("viewFile", contactList_item.get(0));
	    	}
		}
  
		return mv;
	}
	

//	@RequestMapping(value = "/blocko/board/{boardIdx}", method = RequestMethod.PUT)
//	public String updateBoard(BoardEntity board) throws Exception {
//		boardService.saveBoard(board, null);
//		return "redirect:/blocko/board";
//	}

	// 게시글 수정.
	@RequestMapping(value = "/blocko/board/{boardIdx}", method = RequestMethod.POST)
	public String edit(HttpServletResponse response, HttpServletRequest request ,
			MultipartHttpServletRequest mtfRequest) throws Exception {
		response.setContentType("text/html; charset=UTF-8");

		//Map paramMap = request.getParameterMap();
		
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
 
		paramMap.put("boardIdx", request.getParameterValues("boardIdx"));
		paramMap.put("title", request.getParameterValues("title"));
		paramMap.put("contents", request.getParameterValues("contents"));

		//파일 변경 추가 
		List<MultipartFile> files = mtfRequest.getFiles("fileList");
		String[] fileNames = new String[files.size()];
		for (int i=0; i<files.size(); i++) {
			fileNames[i] = files.get(i).getOriginalFilename();
		}
		
 		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd"); 
		ZonedDateTime current = ZonedDateTime.now();
		String path = "images/"+current.format(format);
		//String finalPath = path + "/" +Long.toString(System.nanoTime()) + fileNames[0];
		String fileSize = Integer.toString((int)files.get(0).getSize());
		String[] changePath = path.split(" ");
		String[] changeSize = fileSize.split(" ");
		  
		//paramMap.put("idx", request.getParameterValues("idx"));
		paramMap.put("originalFileName", fileNames);
		paramMap.put("storedFilePath", changePath);
		paramMap.put("fileSize",changeSize);
 	
		
		boardService.edit(paramMap);
		
		return "redirect:/blocko/board";
 
	}

	@RequestMapping(value = "/blocko/board/{boardIdx}", method = RequestMethod.DELETE)
	public String deleteBoard(@PathVariable("boardIdx") int boardIdx) throws Exception {
		boardService.delete(boardIdx);
		//boardService.deleteBoard(boardIdx);
		return "redirect:/blocko/board";
	}

		
	@RequestMapping(value = "/blocko/board/file", method = RequestMethod.GET)
	public void downloadBoardFile(int boardIdx, int idx, HttpServletResponse response) throws Exception {
//		BoardFileEntity file = boardService.selectBoardFileInformation(boardIdx, idx);
// 
//		
//		byte[] files = org.apache.commons.io.FileUtils.readFileToByteArray(new File(file.getStoredFilePath()));
//
//		response.setContentType("application/octet-stream");
//		response.setContentLength(files.length);
//		response.setHeader("Content-Disposition",
//				"attachment; fileName=\"" + URLEncoder.encode(file.getOriginalFileName(), "UTF-8") + "\";");
//		response.setHeader("Content-Transfer-Encoding", "binary");
//
//		response.getOutputStream().write(files);
//		response.getOutputStream().flush();
//		response.getOutputStream().close();
		 
		
	}
}