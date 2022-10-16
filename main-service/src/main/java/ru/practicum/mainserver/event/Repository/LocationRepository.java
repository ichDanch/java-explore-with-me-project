package ru.practicum.mainserver.event.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainserver.event.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

}
