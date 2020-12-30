package cz.city.honest.application.view;

import java.util.HashMap;
import java.util.Map;

import cz.city.honest.application.view.map.ExchangePointMapPresenter;
import cz.city.honest.application.view.map.MapPresenter;
import cz.city.honest.mobile.model.dto.ExchangePoint;
import cz.city.honest.mobile.model.dto.WatchedSubject;

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

