package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.GrooupDTO;
import org.example.dto.StudentDTO;
import org.example.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Student Management", description = "API for managing students")
@Slf4j
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @Operation(summary = "Get students by group name", description = "Enter the group name to find related students")
    @GetMapping("/group/{groupName}")
    public ResponseEntity<List<StudentDTO>> getStudentsByGroupName(@Valid @PathVariable String groupName) {
        List<StudentDTO> students = studentService.getStudentsByGroupName(groupName);
        log.info("Students by group name successfully received");

        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @Operation(summary = "Get students ordered by rating in descending order", description = "Indicate the page number and page size for page numbering")
    @GetMapping("/rating-desc")
    public ResponseEntity<List<StudentDTO>> getStudentsOrderByRatingDesc(@RequestParam(defaultValue = "0") int pageNumber,
                                                                      @RequestParam(defaultValue = "10") int pageSize) {
        List<StudentDTO> students = studentService.getStudentsOrderByRatingDesc(pageNumber, pageSize);
        log.info("Students sorted by rating desc successfully received");

        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @Operation(summary = "Get students with best rating from each group", description = "Find the highest rated students from each group")
    @GetMapping("/best-rating")
    public ResponseEntity<Map<GrooupDTO, List<StudentDTO>>> getStudentsWithBestRatingFromEachGroup() {
        Map<GrooupDTO, List<StudentDTO>> bestStudentsByGroup = studentService.getStudentsWithBestRatingFromEachGroup();
        log.info("Students with best rating from each group successfully received");

        return new ResponseEntity<>(bestStudentsByGroup, HttpStatus.OK);
    }

    @Operation(summary = "Get students with less than average rating from each group", description = "Receive students with ratings below the group average")
    @GetMapping("/less-than-avg-rating")
    public ResponseEntity<Map<GrooupDTO, List<StudentDTO>>> getStudentsWithLessThanAvgRatingFromEachGroup() {
        Map<GrooupDTO, List<StudentDTO>> students = studentService.getStudentsWithLessThanAvgRatingFromEachGroup();
        log.info("Students with less than avg rating from each group successfully received");

        return new ResponseEntity<>(students, HttpStatus.OK);
    }
}
