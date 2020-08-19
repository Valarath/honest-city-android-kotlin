package cz.city.honest.application.view.detail.ui.main;

import android.content.Context;
import android.view.View;
import android.widget.TableRow;

import java.util.HashMap;
import java.util.Map;

import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion;
import cz.city.honest.application.model.dto.ExchangeRateSuggestion;
import cz.city.honest.application.model.dto.NewExchangePointSuggestion;
import cz.city.honest.application.model.dto.Suggestion;

public abstract class SuggestionTableRowConverter<SUGGESTION extends Suggestion> {

    private static final Map<Class<Suggestion>, SuggestionTableRowConverter<Suggestion>> SUGGESTION_TO_TABLE_ROW_CONVERTER_MAP = new HashMap() {{
        put(ClosedExchangePointSuggestion.class, new ClosedExchangePointSuggestionTableRowConverter());
        put(ExchangeRateSuggestion.class, new ExchangeRateSuggestionTableRowConverter());
        put(NewExchangePointSuggestion.class, new NewExchangePointSuggestionTableRowConverter());
    }};

    public static View asTableRow(Suggestion suggestion, Context context) {
        return SUGGESTION_TO_TABLE_ROW_CONVERTER_MAP
                .get(suggestion.getClass())
                .convert(suggestion, context);
    }

    protected TableRow getGenericSuggestionInformationPanel(
            Context context,
            SUGGESTION suggestion
    ) {
        TableRow tableRow = new TableRow(context);
        tableRow.addView(TableRowCreator.Companion.getCell(suggestion.getState().name(), 2f, context));
        tableRow.addView(TableRowCreator.Companion.getCell(suggestion.getVotes(), 1f, context));
        return tableRow;
    }


    protected abstract View convert(SUGGESTION suggestion, Context context);

}
