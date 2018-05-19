package polytechnice.si3.ihm.android.database.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import polytechnice.si3.ihm.android.database.model.User;
import polytechnice.si3.ihm.android.database.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private LiveData<List<User>> users;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        users = userRepository.getAll();
    }

    public LiveData<List<User>> getAll() {
        return users;
    }

    public void insert(User... users) {
        userRepository.insert(users);
    }

    public void delete(User... users) {
        userRepository.delete(users);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

}
