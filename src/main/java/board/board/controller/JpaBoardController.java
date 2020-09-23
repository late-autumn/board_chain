package board.board.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Type;
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
import board.board.service.JpaBoardServiceImpl;
import board.common.FileUtils;
import hera.api.model.ContractResult;

@Controller
public class JpaBoardController {

	@Value("${file.path}")
	private String filepath;

	protected final Logger logger = getLogger(getClass());

	@Autowired
	private JpaBoardServiceImpl jpaBoardService;

	
	@RequestMapping(value = "/jpa/board", method = RequestMethod.GET)
	public ModelAndView openBoardList(HttpServletResponse response, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		response.setContentType("text/html; charset=UTF-8");
		mv.setViewName("board/JpaBoardList");
		ContractResult contractResult = jpaBoardService.list();

		if (!"\"empty\"".equals(contractResult.toString())) {
			Gson gson = new Gson();
			Type type = new TypeToken<List<BoardEntity>>() {
			}.getType();

			List<BoardEntity> list = gson.fromJson(contractResult.toString(), type);
			mv.addObject("list", list);
		}
		return mv;
	}


	@RequestMapping(value = "/jpa/board/write", method = RequestMethod.GET)
	public String openBoardWrite() throws Exception {
		return "/board/jpaBoardWrite";
	}

	// 게시글 생성
	@RequestMapping(value = "/jpa/board/write", method = RequestMethod.POST)
	public String write(HttpServletResponse response, HttpServletRequest request,
			MultipartHttpServletRequest mtfRequest) {
		response.setContentType("text/html; charset=UTF-8");

		// Map paramMap = request.getParameterMap();
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();

		paramMap.put("title", request.getParameterValues("title"));
		paramMap.put("contents", request.getParameterValues("contents"));
		paramMap.put("creatorId", request.getParameterValues("creatorId"));
		//paramMap.put("originalFileName", mtfRequest.getFileNames());
			
			 

		List<MultipartFile> files = mtfRequest.getFiles("fileList");
		String[] fileNames = new String[files.size()];
		for (int i=0; i<files.size(); i++) {
			fileNames[i] = files.get(i).getOriginalFilename();
		}
		logger.info("파일 저장된 이름"+fileNames);
		logger.info("파일 저장된 이름"+fileNames[0]);
		paramMap.put("originalFileName", fileNames);
		
  
		
		int boardIdx = jpaBoardService.write(paramMap);
		 if(mtfRequest.getFile("fileList").getSize() != 0) {
		      jpaBoardService.write_item_images(boardIdx, mtfRequest, filepath, request);
		    }
 
		  
		 
		 
		return "redirect:/jpa/board";
	}

//	@RequestMapping(value="/jpa/board/{boardIdx}", method=RequestMethod.GET)
//	public ModelAndView openBoardDetail(@PathVariable("boardIdx") int boardIdx) throws Exception{
//		ModelAndView mv = new ModelAndView("/board/jpaBoardDetail");
//		
//		BoardEntity board = jpaBoardService.selectBoardDetail(boardIdx);
//		logger.info("변환형태:"+board);
//		mv.addObject("board", board);
//		
//		return mv;
//	}

	@RequestMapping(value = "/jpa/board/{boardIdx}", method = RequestMethod.GET)
	public ModelAndView openBoardDetail(HttpServletResponse response, HttpServletRequest request,
			@PathVariable("boardIdx") int boardIdx) {
		ModelAndView mv = new ModelAndView();
		response.setContentType("text/html; charset=UTF-8");
		mv.setViewName("board/JpaBoardDetail");
		Map paramMap = request.getParameterMap();
		// Optional<BoardEntity> optional = JpaBoardRepository.findById(boardIdx);
		
		jpaBoardService.increaseHitCnt(boardIdx);
		ContractResult contractResult = jpaBoardService.view(boardIdx);		 
 
/*
		JsonParser jsonParser = new JsonParser();
		String jsonStr = contractResult.toString();

		JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonStr);
				
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject object = (JsonObject) jsonArray.get(i);
			String boardIdx2 = object.get("boardIdx").getAsString();
			String title = object.get("title").getAsString();
			String contents = object.get("contents").getAsString();
			String created_datetime = object.get("created_datetime").getAsString();
			String creatorId = object.get("creatorId").getAsString();
			String hitCnt = object.get("hitCnt").getAsString();

			mv.addObject("boardIdx", boardIdx2);
			mv.addObject("title", title);
			mv.addObject("contents", contents);
			mv.addObject("createdDatetime", created_datetime);
			mv.addObject("creatorId", creatorId);
			mv.addObject("hitCnt", hitCnt);
		}
*/		
		if (!"\"empty\"".equals(contractResult.toString())) {
			Gson gson = new Gson();
			Type type = new TypeToken<List<BoardEntity>>() {
			}.getType();
			 
			List<BoardEntity> board = gson.fromJson(contractResult.toString(), type);
			
			mv.addObject("board", board.get(0));
		}
 
		 //FileDetail
		if (!"\"empty\"".equals(contractResult.toString())) {
	    ContractResult contractResult_item = jpaBoardService.view_Image(boardIdx);
	    Gson gson1 = new Gson();
	    Type type1 = new TypeToken<List<BoardFileEntity>>() {
	    }.getType();
	    logger.info("파일 이름"+contractResult_item.toString());
	    List<BoardFileEntity> contactList_item = gson1.fromJson(contractResult_item.toString(), type1);
	    	mv.addObject("viewFile", contactList_item.get(0));
		}
  
		return mv;
	}
	

//	@RequestMapping(value = "/jpa/board/{boardIdx}", method = RequestMethod.PUT)
//	public String updateBoard(BoardEntity board) throws Exception {
//		jpaBoardService.saveBoard(board, null);
//		return "redirect:/jpa/board";
//	}

	// 게시글 수정.
	@RequestMapping(value = "/jpa/board/{boardIdx}", method = RequestMethod.PUT)
	public String edit(HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("text/html; charset=UTF-8");

		//Map paramMap = request.getParameterMap();
		
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
 
		paramMap.put("boardIdx", request.getParameterValues("boardIdx"));
		paramMap.put("title", request.getParameterValues("title"));
		paramMap.put("contents", request.getParameterValues("contents"));

		jpaBoardService.edit(paramMap);
		return "redirect:/jpa/board";
 
	}

	@RequestMapping(value = "/jpa/board/{boardIdx}", method = RequestMethod.DELETE)
	public String deleteBoard(@PathVariable("boardIdx") int boardIdx) throws Exception {
		jpaBoardService.delete(boardIdx);
		//jpaBoardService.deleteBoard(boardIdx);
		return "redirect:/jpa/board";
	}

	
	
//	@RequestMapping(value = "/jpa/board/file", method = RequestMethod.GET)
//	public void downloadBoardFile(int boardIdx, int idx, HttpServletResponse response) throws Exception {
//		BoardFileEntity file = jpaBoardService.selectBoardFileInformation(boardIdx, idx);
//
//		byte[] files = FileUtils.readFileToByteArray(new File(file.getStoredFilePath()));
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
//	}
}