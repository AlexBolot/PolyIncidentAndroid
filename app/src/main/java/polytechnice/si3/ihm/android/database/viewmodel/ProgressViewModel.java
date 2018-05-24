package polytechnice.si3.ihm.android.database.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Optional;

import polytechnice.si3.ihm.android.database.model.Progress;
import polytechnice.si3.ihm.android.database.repository.ProgressRepository;

public class ProgressViewModel extends AndroidViewModel {

    private ProgressRepository progressRepository;
    private LiveData<List<Progress>> progresses;

    public ProgressViewModel(@NonNull Application application) {
        super(application);
        progressRepository = new ProgressRepository(application);
        progresses = progressRepository.getAll();
    }

    public LiveData<List<Progress>> getAll() {
        return progresses;
    }

    public Optional<Progress> getByID(int id) {
        return progressRepository.getByID(id);
    }

    public void insert(Progress... progresses) {
        progressRepository.insert(progresses);
    }

    public void delete(Progress... progresses) {
        progressRepository.delete(progresses);
    }

    public void deleteAll() {
        progressRepository.deleteAll();
    }

}
