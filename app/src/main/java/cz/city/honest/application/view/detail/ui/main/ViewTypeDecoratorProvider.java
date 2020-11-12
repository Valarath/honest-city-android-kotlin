package cz.city.honest.application.view.detail.ui.main;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashMap;
import java.util.Map;

import cz.city.honest.mobile.model.dto.WatchedSubject;

public class ViewTypeDecoratorProvider {

    private static Map<Class<? extends View>, ShowSubjectSuggestionRowDecorator<? extends View>> VIEW_TO_TYPE_DECORATORS = new HashMap<>();

    static {
        VIEW_TO_TYPE_DECORATORS.put(TableRow.class, new ShowSubjectSuggestionTableRowDecorator());
        VIEW_TO_TYPE_DECORATORS.put(TableLayout.class, new ShowSubjectSuggestionTableLayoutDecorator());
    }

    public static <VIEW extends View> ShowSubjectSuggestionRowDecorator provide(Class<VIEW> viewClass) {
        return VIEW_TO_TYPE_DECORATORS.get(viewClass);
    }
}
