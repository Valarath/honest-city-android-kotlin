package cz.city.honest.view.user.ui.main;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashMap;
import java.util.Map;

public class UserDetailSuggestionRowDecoratorProvider {

    private static Map<Class<? extends View>, UserDetailSuggestionRowDecorator<? extends View>> VIEW_TO_TYPE_DECORATORS = new HashMap<>();

    static {
        VIEW_TO_TYPE_DECORATORS.put(TableRow.class, new UserDetailSuggestionTableRowDecorator());
        VIEW_TO_TYPE_DECORATORS.put(TableLayout.class, new UserDetailSuggestionTableLayoutDecorator());
    }

    public static <VIEW extends View> UserDetailSuggestionRowDecorator provide(Class<VIEW> viewClass) {
        return VIEW_TO_TYPE_DECORATORS.get(viewClass);
    }
}
