package dev.ydkulks.TheDrip.repos;

import org.springframework.data.repository.CrudRepository;

import dev.ydkulks.TheDrip.models.UserModel;

public interface UserRepo extends CrudRepository<UserModel, Long> {
}
