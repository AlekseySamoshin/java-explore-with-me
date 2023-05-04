package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {


    @Query("select u from User u " +
            "where u.id in :ids")
    List<User> findAllByIdsPageable(List<Long> ids, Pageable page);
}
