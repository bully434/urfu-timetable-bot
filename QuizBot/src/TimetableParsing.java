import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class TimetableParsing
{
    String[] WeekDays = new String[] { "Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг",
            "Пятница", "Суббота" };

    public static Calendar ReadFile(String filename)
    {
        Calendar calendar = null;
        try
        {
            var fileStream = new FileInputStream(filename);
            var builder = new CalendarBuilder();
            calendar = builder.build(fileStream);
        }
        catch (Exception e){}
        return calendar;
    }

    public HashMap<String, ArrayList<Subject>> CreateTimeTableDataBase(Calendar calendar)
    {
        var timetable = new HashMap<String, ArrayList<Subject>>();
        for (String day: WeekDays) {
            timetable.put(day, new ArrayList<>());
        }
        ComponentList listEvent = calendar.getComponents(Component.VEVENT);

        for (Object elem : listEvent)
        {
            VEvent event = (VEvent) elem;
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            var subject = event.getSummary().getValue();
            var dateStart = event.getStartDate().getDate();
            var dateEnd = event.getEndDate().getDate();
            var dateStartTime = timeFormat.format(dateStart);
            var dateEndTime = timeFormat.format(dateEnd);
            var weekday = DetermineDay(dateStart);
            String description = null;
            try{
                description = event.getDescription().getValue();
            }
            catch (Exception e){}
            var currentSubject = new Subject(weekday, subject, dateStartTime, dateEndTime, description);
            var day = timetable.get(weekday);
            day.add(currentSubject);
        }
        return timetable;
    }
    public String DetermineDay(Date date){
        var dateParser = java.util.Calendar.getInstance();
        dateParser.setTime(date);
        return WeekDays[dateParser.get(java.util.Calendar.DAY_OF_WEEK) - 1];
    }

    public static void main(String[] args)
    {
        var parser = new TimetableParsing();
        var calendar = parser.ReadFile("./QuizBot/DataBase/calendar.ics");
        var timetable = parser.CreateTimeTableDataBase(calendar);
    }
}
