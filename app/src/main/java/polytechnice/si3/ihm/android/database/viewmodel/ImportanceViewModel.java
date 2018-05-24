package polytechnice.si3.ihm.android.database.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import polytechnice.si3.ihm.android.database.model.Importance;
import polytechnice.si3.ihm.android.database.repository.ImportanceRepository;

public class ImportanceViewModel extends AndroidViewModel {

    private ImportanceRepository importanceRepository;
    private LiveData<List<Importance>> importances;

    public ImportanceViewModel(@NonNull Application application) {
        super(application);
        importanceRepository = new ImportanceRepository(application);
        importances = importanceRepository.getAll();
    }

    public LiveData<List<Importance>> getAll() {
        return importances;
    }

    public LiveData<Importance> getByID(int id) {
        return importanceRepository.getByID(id);
    }

    public void insert(Importance... importances) {
        importanceRepository.insert(importances);
    }

    public void delete(Importance... importances) {
        importanceRepository.delete(importances);
    }

    public void deleteAll() {
        importanceRepository.deleteAll();
    }

}
