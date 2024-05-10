package vn.ndc.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ndc.jobhunter.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
