package cz.city.honest.application.view.detail.ui.main;

import android.content.Context;
import android.view.View;
import android.widget.TableRow;

import java.util.HashMap;
import java.util.Map;

import cz.city.honest.application.R;
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion;
import cz.city.honest.application.model.dto.ExchangeRateSuggestion;
import cz.city.honest.application.model.dto.NewExchangePointSuggestion;
import cz.city.honest.application.model.dto.State;
import cz.city.honest.application.model.dto.Suggestion;
import cz.city.honest.application.model.dto.UserSuggestion;
import cz.city.honest.application.viewmodel.VotedSuggestion;

public abstract class SuggestionTableRowConverter<SUGGESTION extends Suggestion> {

    private static final Map<Class<Suggestion>, SuggestionTableRowConverter<Suggestion>> SUGGESTION_TO_TABLE_ROW_CONVERTER_MAP = new HashMap() {{
        put(ClosedExchangePointSuggestion.class, new ClosedExchangePointSuggestionTableRowConverter());
        put(ExchangeRateSuggestion.class, new ExchangeRateSuggestionTableRowConverter());
        put(NewExchangePointSuggestion.class, new NewExchangePointSuggestionTableRowConverter());
    }};

    protected static final Map<State, Integer> stateColor = new HashMap(){{
        put(State.ACCEPTED,R.color.suggestionAccepted);
        put(State.DECLINED,R.color.suggestionDeclined);
        put(State.IN_PROGRESS,R.color.suggestionInProgress);
    }};

    public static View asTableRow(VotedSuggestion suggestion, Context context) {
        return SUGGESTION_TO_TABLE_ROW_CONVERTER_MAP
                .get(suggestion.getSuggestion().getClass())
                .convert(suggestion.getSuggestion(), context);
    }

    public static View asTableRow(UserSuggestion suggestion, Context context) {
        return SUGGESTION_TO_TABLE_ROW_CONVERTER_MAP
                .get(suggestion.getSuggestion().getClass())
                .convert(suggestion.getSuggestion(), context);
    }

    protected TableRow getGenericSuggestionInformationPanel(
            Context context,
            SUGGESTION suggestion
    ) {
        TableRow tableRow = new TableRow(context);
        tableRow.setBackgroundColor(context.getColor(stateColor.get(suggestion.getState())));
        tableRow.addView(TableRowCreator.Companion.getCell(suggestion.getState().name(), 2f, context));
        tableRow.addView(TableRowCreator.Companion.getCell(suggestion.getVotes(), 1f, context));
        return tableRow;
    }

    protected abstract View convert(SUGGESTION suggestion, Context context);
}
