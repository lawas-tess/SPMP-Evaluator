package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.StudentProfessorAssignment;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.StudentProfessorAssignmentRepository;
import com.team02.spmpevaluator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing student-professor assignments.
 * UC 2.12: Admin Assign Students to Professors
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {
    
    private final StudentProfessorAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public StudentProfessorAssignment assignStudentToProfessor(Long studentId, Long professorId, Long adminId, String notes) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("User is not a student");
        }
        if (professor.getRole() != Role.PROFESSOR) {
            throw new IllegalArgumentException("User is not a professor");
        }
        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("User is not an admin");
        }

        // Check if assignment already exists
        if (assignmentRepository.existsByStudent_IdAndProfessor_Id(studentId, professorId)) {
            throw new IllegalArgumentException("Student is already assigned to this professor");
        }

        StudentProfessorAssignment assignment = new StudentProfessorAssignment();
        assignment.setStudent(student);
        assignment.setProfessor(professor);
        assignment.setAssignedBy(admin);
        assignment.setNotes(notes);

        return assignmentRepository.save(assignment);
    }

    public List<StudentProfessorAssignment> getAssignmentsByProfessor(Long professorId) {
        return assignmentRepository.findByProfessor_Id(professorId);
    }

    public List<User> getStudentsByProfessor(Long professorId) {
        return assignmentRepository.findStudentsByProfessorId(professorId);
    }

    public User getProfessorByStudent(Long studentId) {
        return assignmentRepository.findProfessorByStudentId(studentId).orElse(null);
    }

    public void removeAssignment(Long assignmentId) {
        assignmentRepository.deleteById(assignmentId);
    }

    public List<StudentProfessorAssignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }
}
