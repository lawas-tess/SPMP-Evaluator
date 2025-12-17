package com.team02.spmpevaluator.repository;

import com.team02.spmpevaluator.entity.StudentProfessorAssignment;
import com.team02.spmpevaluator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfessorAssignmentRepository extends JpaRepository<StudentProfessorAssignment, Long> {
    
    List<StudentProfessorAssignment> findByProfessor_Id(Long professorId);
    
    List<StudentProfessorAssignment> findByStudent_Id(Long studentId);
    
    Optional<StudentProfessorAssignment> findByStudent_IdAndProfessor_Id(Long studentId, Long professorId);
    
    boolean existsByStudent_IdAndProfessor_Id(Long studentId, Long professorId);
    
    @Query("SELECT spa.student FROM StudentProfessorAssignment spa WHERE spa.professor.id = :professorId")
    List<User> findStudentsByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT spa.professor FROM StudentProfessorAssignment spa WHERE spa.student.id = :studentId")
    Optional<User> findProfessorByStudentId(@Param("studentId") Long studentId);
    
    void deleteByStudentId(Long studentId);
    
    void deleteByProfessorId(Long professorId);
}
