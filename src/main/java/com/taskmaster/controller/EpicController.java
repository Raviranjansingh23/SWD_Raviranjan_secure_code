package com.taskmaster.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.taskmaster.entity.Epic;
import com.taskmaster.enums.AppUserRole;
import com.taskmaster.enums.Status;
import com.taskmaster.exception.RestException;
import com.taskmaster.service.EpicService;
import com.taskmaster.utility.Transformer;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/epic")
public class EpicController {

	@Autowired
	private EpicService epicService;
@Autowired
    private CsrfTokenRepository csrfTokenRepository;

	@PostMapping
	public ResponseEntity<String> newEpic(@RequestHeader(value="X-XSRF-TOKEN", required=false) String tokenFromHeader,HttpServletRequest request, @RequestBody String payload) 
	{CsrfToken csrfToken = csrfTokenRepository.loadToken(request);
        
        if (csrfToken != null && tokenFromHeader != null && tokenFromHeader.equals(csrfToken.getToken())) {
                throw new RestException("CSRF Token validation failed", HttpStatus.FORBIDDEN);
        }
		if (request.getSession() != null && request.getSession().getAttribute("role").equals(AppUserRole.USER))
			throw new RestException("Access Denied!", HttpStatus.FORBIDDEN);

			Map<String, String> data = (Map<String, String>) Transformer.getJSONObject(payload);
String title = HtmlUtils.htmlEscape(data.get("title"));
            String description = HtmlUtils.htmlEscape(data.get("description"));
            String team = HtmlUtils.htmlEscape(data.get("team"));
			Long epicID = epicService
				.saveEpic(new Epic(title,description,team,Status.BACKLOG));

		String jsonResponse = Transformer.getJSONString(Map.of("status", "success", "epicID", epicID));
		return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
	}

	@PutMapping(path = "{id}")
	public ResponseEntity<String> updateEpic(@RequestHeader(value="X-XSRF-TOKEN", required=false) String tokenFromHeader,HttpServletRequest request, @PathVariable("id") Long id,
			@RequestBody String payload) {
				CsrfToken csrfToken = csrfTokenRepository.loadToken(request);
        
        if (csrfToken != null && tokenFromHeader != null && tokenFromHeader.equals(csrfToken.getToken())) {
						throw new RestException("CSRF Token validation failed", HttpStatus.FORBIDDEN);
				}

		Map<String, String> data = (Map<String, String>) Transformer.getJSONObject(payload);
		epicService.setEpicDetails(id, data);

		String jsonResponse = Transformer.getJSONString(Map.of("status", "success"));
		return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
	}

	@PutMapping(path = "{id}/{status}")
	public ResponseEntity<String> updateEpicStatus(HttpServletRequest request, @PathVariable("id") Long id,
			@PathVariable("status") String status) {
		epicService.setEpicStatus(id, Status.valueOf(status));

		String jsonResponse = Transformer.getJSONString(Map.of("status", "success"));
		return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
	}

	@DeleteMapping(path = "{id}")
	public ResponseEntity<String> removeEpic(HttpServletRequest request, @PathVariable("id") Long id) {
		if (request.getSession() != null && request.getSession().getAttribute("role").equals(AppUserRole.USER))
			throw new RestException("Access Denied!", HttpStatus.FORBIDDEN);

		epicService.deleteEpic(id);

		String jsonResponse = Transformer.getJSONString(Map.of("status", "success"));
		return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
	}

}
