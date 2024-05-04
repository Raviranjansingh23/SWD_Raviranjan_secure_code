package com.taskmaster.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.taskmaster.entity.Epic;
import com.taskmaster.enums.Status;
import com.taskmaster.repository.EpicRepo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class EpicService {

	@Autowired
	private EpicRepo epicRepository;

	public Epic getEpicById(Long id) {
		return epicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Epic not found"));
	}

	public List<Epic> getEpicsByStatus(Status status) {
		return epicRepository.findByStatus(status);
	}
	
	public List<Epic> getAllEpics() {
		return epicRepository.findAll();
	}

	public Long saveEpic(Epic epic) {
		return epicRepository.save(epic).getId();
	}

	@Transactional
	public void setEpicDetails(Long id, Map<String, String> data) {
		
		Epic epic = epicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Epic not found"));
		String title = HtmlUtils.htmlEscape(data.get("title"));
            String description = HtmlUtils.htmlEscape(data.get("description"));
            String team = HtmlUtils.htmlEscape(data.get("team"));
		epic.setTitle(title);
		epic.setDescription(description);
		epic.setAssignedTeam(team);
	}

	@Transactional
	public void setEpicStatus(Long id, Status status) {
		Epic epic = epicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Epic not found"));
		epic.setStatus(status);
	}

	public void deleteEpic(Long id) {
		epicRepository.deleteById(id);
	}

}
