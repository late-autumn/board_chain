package board.board.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import board.board.entity.BoardEntity;
import board.board.entity.BoardFileEntity;
import board.board.service.JpaBoardServiceImpl;
import hera.api.model.ContractResult;


@Controller
public class JpaBoardController {
	
	//Value("${file.path}")
	private String filepath;
	
	protected final Logger logger = getLogger(getClass());
	
	@Autowired
	private JpaBoardServiceImpl jpaBoardService;
	
//	@RequestMapping(value="/jpa/board", method=RequestMethod.GET)
//	public ModelAndView openBoardList(ModelMap model) throws Exception{
//		ModelAndView mv = new ModelAndView("/board/jpaBoardList");
// 		
//		List<BoardEntity> list = jpaBoardService.selectBoardList();
//		mv.addObject("list", list);
//		
//		return mv;
//	}
	
	@RequestMapping(value="/jpa/board", method=RequestMethod.GET)
	public ModelAndView home(HttpServletResponse response, HttpServletRequest request) {
		    ModelAndView mv = new ModelAndView();
//		    response.setContentType("text/html; charset=UTF-8");
		    mv.setViewName("/board/jpaBoardList");
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
	
	 
	
	
	
	@RequestMapping(value="/jpa/board/write", method=RequestMethod.GET)
	public String openBoardWrite() throws Exception{
		return "/board/jpaBoardWrite";
	}
	
//	@RequestMapping(value="/jpa/board/write", method=RequestMethod.POST)
//	public String writeBoard(BoardEntity board, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception{
//		jpaBoardService.saveBoard(board, multipartHttpServletRequest);		 
//		return "redirect:/jpa/board";
//	}
	
 
//	  @RequestMapping(value="/jpa/board/write", method=RequestMethod.GET)
//	  public ModelAndView writeForm(HttpServletResponse response, HttpServletRequest request) {
//	    ModelAndView mv = new ModelAndView();
//	    response.setContentType("text/html; charset=UTF-8");
//	    mv.setViewName("admin/vote/voteWriteForm");
//	    return mv;
//	  }

	  //투표 생성.
	  @RequestMapping(value="/jpa/board/write", method=RequestMethod.POST)
	  public String write(HttpServletResponse response, HttpServletRequest request, MultipartHttpServletRequest mtfRequest) {
//	    response.setContentType("text/html; charset=UTF-8");
	    Map paramMap = request.getParameterMap(); 

	    logger.debug("======= ***** =======paramMap : {}", paramMap.get("title"));
	    logger.debug("======= ***** =======paramMap : {}", paramMap.get("contents"));
	    int boardIdx = jpaBoardService.write(paramMap);
	    
	    return "redirect:/jpa/board";
	  }
	
	
	
	
	
	
	
	
	
	
	
	
//	@RequestMapping(value="/jpa/board/{boardIdx}", method=RequestMethod.GET)
//	public ModelAndView openBoardDetail(@PathVariable("boardIdx") int boardIdx) throws Exception{
//		ModelAndView mv = new ModelAndView("/board/jpaBoardDetail");
//		
//		BoardEntity board = jpaBoardService.selectBoardDetail(boardIdx);
//		mv.addObject("board", board);
//		
//		return mv;
//	}
	
	@RequestMapping(value="/jpa/board/{boardIdx}", method=RequestMethod.GET)
	  public ModelAndView voteView(HttpServletResponse response, HttpServletRequest request, @PathVariable("boardIdx") Integer boardIdx) {
	    ModelAndView mv = new ModelAndView();
//	    response.setContentType("text/html; charset=UTF-8");
	    mv.setViewName("/board/jpaBoardDetail");
	    Map paramMap = request.getParameterMap();
	    ContractResult contractResult = jpaBoardService.view(boardIdx);
 
	    Gson gson = new Gson();
	    Type type = new TypeToken<List<BoardEntity>>() {
	    }.getType();
	    List<BoardEntity> board = gson.fromJson(contractResult.toString(), type);
  

	    mv.addObject("board", board);

	    return mv;
	  }
	
	 
	
	
	
	
	@RequestMapping(value="/jpa/board/{boardIdx}", method=RequestMethod.PUT)
	public String updateBoard(BoardEntity board) throws Exception{
		jpaBoardService.saveBoard(board, null);
		return "redirect:/jpa/board";
	}
	
	
	//투표 수정.
	  @RequestMapping(method = RequestMethod.POST, value = "edit")
	  public String edit(HttpServletResponse response, HttpServletRequest request) {
	    response.setContentType("text/html; charset=UTF-8");

	    Map paramMap = request.getParameterMap();
 

	    jpaBoardService.edit(paramMap);
	    return "redirect:/";
	  }
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value="/jpa/board/{boardIdx}", method=RequestMethod.DELETE)
	public String deleteBoard(@PathVariable("boardIdx") int boardIdx) throws Exception{
		jpaBoardService.deleteBoard(boardIdx);
		return "redirect:/jpa/board";
	}
	
	@RequestMapping(value="/jpa/board/file", method=RequestMethod.GET)
	public void downloadBoardFile(int boardIdx, int idx, HttpServletResponse response) throws Exception{
		BoardFileEntity file = jpaBoardService.selectBoardFileInformation(boardIdx, idx); 
		
		byte[] files = FileUtils.readFileToByteArray(new File(file.getStoredFilePath()));
		
		response.setContentType("application/octet-stream");
		response.setContentLength(files.length);
		response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(file.getOriginalFileName(),"UTF-8")+"\";");
		response.setHeader("Content-Transfer-Encoding", "binary");
		
		response.getOutputStream().write(files);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
}