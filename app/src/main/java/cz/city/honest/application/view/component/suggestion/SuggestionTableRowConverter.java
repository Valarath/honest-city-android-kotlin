package cz.city.honest.application.view.component.suggestion;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TableRow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cz.city.honest.application.R;
import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion;
import cz.city.honest.application.model.dto.ExchangeRateSuggestion;
import cz.city.honest.application.model.dto.NewExchangePointSuggestion;
import cz.city.honest.application.model.dto.State;
import cz.city.honest.application.model.dto.Suggestion;
import cz.city.honest.application.model.dto.UserSuggestion;
import cz.city.honest.application.view.detail.ui.main.TableRowCreator;
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
        setTableRowStyle(suggestion, tableRow);
        tableRow.addView(TableRowCreator.Companion.getCell(getTranslation(context,suggestion), 32f, context));
        tableRow.addView(TableRowCreator.Companion.getCell(suggestion.getState().name(), 32f, context));
        tableRow.addView(TableRowCreator.Companion.getCell(suggestion.getVotes(), 15f, context));
        tableRow.addView(TableRowCreator.Companion.getCell( 15f, context));
        return tableRow;
    }

    private void setTableRowStyle(SUGGESTION suggestion, TableRow tableRow) {
        tableRow.setBackground(getBackground(tableRow.getContext(),suggestion.getState()));
        tableRow.setGravity(Gravity.CENTER);
    }

    private LayerDrawable getBackground(Context context, State state){
        LayerDrawable background = (LayerDrawable)context.getDrawable(R.drawable.suggestion_row);
        GradientDrawable backgroundHolder =(GradientDrawable)background.findDrawableByLayerId(R.id.suggestion_background_holder);
        backgroundHolder.setColor(context.getColor(stateColor.get(state)));
        return background;
    }

    private String getTranslation(Context context, SUGGESTION suggestion){
        return Arrays.stream(context.getResources().getStringArray(R.array.suggestions_translations))
                .filter(it -> it.startsWith(suggestion.getClassName()))
                .map(it -> it.split(context.getResources().getString(R.string.delimiter))[1])
                .findFirst()
                .orElse("");
    }

    protected abstract View convert(SUGGESTION suggestion, Context context);
}
