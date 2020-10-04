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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import board.board.entity.AccountEntity;
import board.board.entity.BoardEntity;
import board.board.entity.BoardFileEntity;
import board.board.service.AccountServiceImpl;
import board.board.service.BoardServiceImpl;
import board.common.FileUtils;
import hera.api.model.ContractResult;

@Controller
public class AccountController {

	protected final Logger logger = getLogger(getClass());

	@Autowired
	private AccountServiceImpl accountService;

	@RequestMapping(value = "/blocko/login", method = RequestMethod.GET)
	public String openlogin() throws Exception {
		return "/login/login";
	}

	// 로그인
	@RequestMapping(value = "/blocko/login", method = RequestMethod.POST)
	public String login(HttpServletResponse response, HttpServletRequest request,
			MultipartHttpServletRequest mtfRequest, @PathVariable("email") String email,
			String password) throws Exception {
 		response.setContentType("text/html; charset=UTF-8");
 		
		Map paramMap = request.getParameterMap();
		// Optional<BoardEntity> optional = blockoBoardRepository.findById(boardIdx);
		
 		ContractResult contractResult = accountService.login(email,password);		 
 
		if (!"\"empty\"".equals(contractResult.toString())) {
			Gson gson = new Gson();
			Type type = new TypeToken<List<AccountEntity>>() {
			}.getType();
			 
			List<AccountEntity> board = gson.fromJson(contractResult.toString(), type);
			return "redirect:/blocko/board";
			 
		}
		else {
			return "redirect:/blocko/login";
		}
 	}
	 
	 
	
	
	

	@RequestMapping(value = "/blocko/register", method = RequestMethod.GET)
	public String openJoin() throws Exception {
		return "/login/register";
	}

	// 회원 가입
	@RequestMapping(value = "/blocko/register", method = RequestMethod.POST)
	public String join(HttpServletResponse response, HttpServletRequest request, String email) throws Exception {
		response.setContentType("text/html; charset=UTF-8");

		ContractResult contractResult = accountService.checkAccountEmail(email);

		if (!"\"empty\"".equals(contractResult.toString())) {

			// Map paramMap = request.getParameterMap();
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();

			paramMap.put("userName", request.getParameterValues("userName"));
			paramMap.put("email", request.getParameterValues("email"));
			paramMap.put("password", request.getParameterValues("password"));
			paramMap.put("auth", request.getParameterValues("auth"));
			
			int idx = accountService.register(paramMap);
		}
		return "redirect:/blocko/login";
	}

}