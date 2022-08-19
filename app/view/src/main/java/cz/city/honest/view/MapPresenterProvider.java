package cz.city.honest.view;

import java.util.HashMap;
import java.util.Map;

import cz.city.honest.dto.ExchangePoint;
import cz.city.honest.dto.WatchedSubject;
import cz.city.honest.view.map.ExchangePointMapPresenter;
import cz.city.honest.view.map.MapPresenter;

public class MapPresenterProvider {

    private final static Map<Class<? extends WatchedSubject>, MapPresenter<? extends WatchedSubject>> showOnMapBySubjectTypeMap = new HashMap<>();

    static {
        showOnMapBySubjectTypeMap.put(
                ExchangePoint.class, new ExchangePointMapPresenter()
        );
    }

    public static <Subject extends WatchedSubject> MapPresenter provide(Class<Subject> subjectClass) {
        return showOnMapBySubjectTypeMap.get(subjectClass);
    }
}

