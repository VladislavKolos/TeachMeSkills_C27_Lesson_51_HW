package org.example.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Grooup;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupValidator {
    private final HibernateUtil hibernateUtil;

    public boolean isGroupExist(String groupName) {
        SessionFactory sessionFactory = hibernateUtil.buildSessionFactory();
        Session session = null;
        boolean result = false;

        try {
            session = sessionFactory.openSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<Grooup> grooupRoot = criteriaQuery.from(Grooup.class);
            criteriaQuery.select(criteriaBuilder.count(grooupRoot)).where(criteriaBuilder.equal(grooupRoot.get("title"),
                    groupName));
            Long count = session.createQuery(criteriaQuery).getSingleResult();

            result = count > 0;

        } catch (Exception e) {
            log.error("There was an error getting the group name: " + e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return result;
    }
}
