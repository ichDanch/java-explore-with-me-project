package ru.practicum.mainserver.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainserver.user.model.User;

import java.util.Collection;


public interface UsersRepository extends JpaRepository<User, Long> {

    Page<User> findAllByIdIn(Collection<Long> ids, Pageable pageable);
}
