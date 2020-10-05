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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import board.board.entity.AccountEntity;
import board.board.service.AccountServiceImpl;
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
	public String login(HttpServletResponse response, HttpServletRequest request) throws Exception {
 		response.setContentType("text/html; charset=UTF-8");

		Map paramMap = request.getParameterMap();
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		// Optional<BoardEntity> optional = blockoBoardRepository.findById(boardIdx);
 
 		ContractResult contractResult = accountService.login(email,password);		 
 
 		if ("\"empty\"".equals(contractResult.toString())|| ("{}".equals(contractResult.toString()))) {
 			return "redirect:/blocko/login";
 		}
 		else {
 			Gson gson = new Gson();
 			Type type = new TypeToken<List<AccountEntity>>() { 				
 			}.getType();
 			List<AccountEntity> account = gson.fromJson(contractResult.toString(), type);
 			return "redirect:/blocko/board";
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

		if ("\"empty\"".equals(contractResult.toString())|| ("{}".equals(contractResult.toString()))) {


			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			
 
			paramMap.put("userName", request.getParameterValues("userName"));
			paramMap.put("email", request.getParameterValues("email"));
			paramMap.put("password", request.getParameterValues("password"));
			paramMap.put("auth", request.getParameterValues("auth"));
			
			int idx = accountService.register(paramMap);
			return "redirect:/blocko/login";
		}
		else {
			logger.info("이미 있다는 의미");
			return "failed";
//			return "redirect:/blocko/register";
		}
		
	}

}