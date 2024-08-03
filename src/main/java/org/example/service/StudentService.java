package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.GrooupDTO;
import org.example.dto.RecordBookDTO;
import org.example.dto.StudentDTO;
import org.example.model.Grooup;
import org.example.model.RecordBook;
import org.example.model.Student;
import org.example.util.HibernateUtil;
import org.example.validator.GroupValidator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    private final HibernateUtil hibernateUtil;
    private final GroupValidator groupValidator;

    public List<StudentDTO> getStudentsByGroupName(String groupName) {
        if (!groupValidator.isGroupExist(groupName)) {
            log.error("There is no group named " + groupName + " in the database");
            throw new IllegalArgumentException("Group with name " + groupName + " does not exist");
        }

        SessionFactory sessionFactory = hibernateUtil.buildSessionFactory();
        Session session = null;
        List<Student> students = new ArrayList<>();

        try {
            session = sessionFactory.openSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
            Root<Student> studentRoot = criteriaQuery.from(Student.class);
            Join<Object, Object> join = studentRoot.join("grooup");
            Predicate predicate = criteriaBuilder.equal(join.get("title"), groupName);
            criteriaQuery.where(predicate);

            students = session.createQuery(criteriaQuery).getResultList();

        } catch (Exception e) {
            log.error("An error occurred when getting a students by group name: " + e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        if (students.isEmpty()) {
            log.error("There are no students in the " + groupName + " group");
            throw new RuntimeException("There are no students in the " + groupName + " group");
        }
        return students.stream().map(this::convertToStudentDto).collect(Collectors.toList());
    }

    public List<StudentDTO> getStudentsOrderByRatingDesc(int pageNumber, int pageSize) {
        SessionFactory sessionFactory = hibernateUtil.buildSessionFactory();
        Session session = null;
        List<Student> students = new ArrayList<>();

        try {
            session = sessionFactory.openSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
            Root<Student> studentRoot = criteriaQuery.from(Student.class);
            Order order = criteriaBuilder.desc(studentRoot.get("recordBook").get("rating"));
            criteriaQuery.orderBy(order);

            Query<Student> query = session.createQuery(criteriaQuery);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            students = query.getResultList();

        } catch (Exception e) {
            log.error("An error occurred when getting a students by rating desc: " + e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        if (students.isEmpty()) {
            log.error("There are no students ordered by rating DESC");
            throw new RuntimeException("There are no students ordered by rating DESC");
        }
        return students.stream().map(this::convertToStudentDto).collect(Collectors.toList());
    }

    public Map<GrooupDTO, List<StudentDTO>> getStudentsWithBestRatingFromEachGroup() {
        SessionFactory sessionFactory = hibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession();
        Map<Grooup, List<Student>> bestStudentsByGroup = new HashMap<>();

        try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Grooup> grooupCriteriaQuery = criteriaBuilder.createQuery(Grooup.class);
            Root<Grooup> grooupRoot = grooupCriteriaQuery.from(Grooup.class);
            grooupCriteriaQuery.select(grooupRoot);
            List<Grooup> grooups = session.createQuery(grooupCriteriaQuery).getResultList();

            bestStudentsByGroup = grooups.stream()
                    .collect(Collectors.toMap(
                            grooup -> grooup,
                            grooup -> {
                                CriteriaQuery<Student> studentCriteriaQuery = criteriaBuilder.createQuery(Student.class);
                                Root<Student> studentRoot = studentCriteriaQuery.from(Student.class);
                                studentCriteriaQuery.where(criteriaBuilder.equal(studentRoot.get("grooup"), grooup));

                                Order order = criteriaBuilder.desc(studentRoot.get("recordBook").get("rating"));
                                studentCriteriaQuery.orderBy(order);

                                Query<Student> query = session.createQuery(studentCriteriaQuery);
                                query.setMaxResults(3);

                                return query.getResultList();
                            }
                    ));

        } catch (Exception e) {
            log.error("An error occurred when getting a students with best rating from each group: " + e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        if (bestStudentsByGroup.isEmpty()) {
            log.error("There are no students with best rating from each group");
            throw new RuntimeException("There are no students with best rating from each group");
        }
        return bestStudentsByGroup.entrySet().stream().collect(Collectors.toMap(
                entry -> convertToGrooupDto(entry.getKey()),
                entry -> entry.getValue().stream().map(this::convertToStudentDto).collect(Collectors.toList())
        ));
    }

    public Map<GrooupDTO, List<StudentDTO>> getStudentsWithLessThanAvgRatingFromEachGroup() {
        SessionFactory sessionFactory = hibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession();
        Map<Grooup, List<Student>> students = new HashMap<>();

        try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Grooup> grooupCriteriaQuery = criteriaBuilder.createQuery(Grooup.class);
            Root<Grooup> grooupRoot = grooupCriteriaQuery.from(Grooup.class);
            grooupCriteriaQuery.select(grooupRoot);
            List<Grooup> grooups = session.createQuery(grooupCriteriaQuery).getResultList();

            students = grooups.stream()
                    .collect(Collectors.toMap(
                            grooup -> grooup,
                            grooup -> {
                                CriteriaQuery<Double> avgRatingQuery = criteriaBuilder.createQuery(Double.class);
                                Root<Student> studentRoot = avgRatingQuery.from(Student.class);
                                avgRatingQuery.select(criteriaBuilder.avg(studentRoot.get("recordBook").get("rating")))
                                        .where(criteriaBuilder.equal(studentRoot.get("grooup"), grooup));
                                Double avgRating = session.createQuery(avgRatingQuery).getSingleResult();

                                CriteriaQuery<Student> studentCriteriaQuery = criteriaBuilder.createQuery(Student.class);
                                studentRoot = studentCriteriaQuery.from(Student.class);
                                Predicate[] predicates = new Predicate[2];
                                predicates[0] = criteriaBuilder.equal(studentRoot.get("grooup"), grooup);
                                predicates[1] = criteriaBuilder.lt(studentRoot.get("recordBook").get("rating"),
                                        avgRating);
                                studentCriteriaQuery.where(criteriaBuilder.and(predicates));

                                return session.createQuery(studentCriteriaQuery).getResultList();
                            }
                    ));

        } catch (Exception e) {
            log.error("An error occurred when getting a students with less than AVG rating from each group: " + e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        if (students.isEmpty()) {
            log.error("There are no students with less than AVG rating from each group");
            throw new RuntimeException("There are no students with less than AVG rating from each group");
        }
        return students.entrySet().stream().collect(Collectors.toMap(
                entry -> convertToGrooupDto(entry.getKey()),
                entry -> entry.getValue().stream().map(this::convertToStudentDto).collect(Collectors.toList())
        ));
    }

    private StudentDTO convertToStudentDto(Student student) {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setName(student.getName());
        studentDTO.setAge(student.getAge());
        studentDTO.setRecordBookDTO(convertToRecordBookDto(student.getRecordBook()));
        studentDTO.setGrooupDTO(convertToGrooupDto(student.getGrooup()));

        return studentDTO;
    }

    private GrooupDTO convertToGrooupDto(Grooup grooup) {
        GrooupDTO grooupDTO = new GrooupDTO();
        grooupDTO.setId(grooup.getId());
        grooupDTO.setTittle(grooup.getTitle());
        grooupDTO.setRoom(grooup.getRoom());

        return grooupDTO;
    }

    private RecordBookDTO convertToRecordBookDto(RecordBook recordBook) {
        RecordBookDTO recordBookDTO = new RecordBookDTO();
        recordBookDTO.setId(recordBook.getId());
        recordBookDTO.setRating(recordBook.getRating());

        return recordBookDTO;
    }
}
