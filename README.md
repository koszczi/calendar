# calendar
A simple calendar implementation

# Features / API endpoints

## Event creation

`POST http://<host>/events/create`

Input body format: `{"startDateTime": "2023-10-13T11:00:00", "endDateTime": "2023-10-13T13:00:00", "organizer": "just me"}`

Output example 1 - input validation errors (response code is `400 - Bad Request`):
```
{
    "status": "VALIDATION_FAILURE",
    "errors": [
        {
            "errorCode": "EVENT_TOO_LONG",
            "errorMessage": "Event is too long"
        },
        {
            "errorCode": "INVALID_TIME",
            "errorMessage": "Events can start/end at XX:00:00 or XX:30:00"
        },
        {
            "errorCode": "OUT_OF_TIMERANGE",
            "errorMessage": "Events can be created between 9:30 and 17:00"
        },
        {
            "errorCode": "NOT_WEEKDAY",
            "errorMessage": "Events can be created only for weekdays"
        }
    ],
    "overlappingEvents": [],
    "newEvent": null
}
```

Output example 2 - overlapping with existing events (response code is `400 - Bad Request`):
```
{
    "status": "VALIDATION_FAILURE",
    "errors": [
        {
            "errorCode": "OVERLAPPING_EVENTS",
            "errorMessage": "Event would overlap with other event(s)"
        }
    ],
    "overlappingEvents": [
        "Event on 2023-10-13, from 09:00 to 12:00, organized by just me",
        "Event on 2023-10-13, from 12:00 to 13:00, organized by just me"
    ],
    "newEvent": null
}
```

Output example 3 - successfully created event (response code is `200 - Ok`):
```
{
    "status": "SUCCESS",
    "errors": [],
    "overlappingEvents": [],
    "newEvent": {
        "id": 3,
        "year": 2023,
        "week": 41,
        "dayOfWeek": "WEDNESDAY",
        "date": "2023-10-11",
        "start": "10:00:00",
        "end": "12:00:00",
        "organizer": "just me"
    }
}
```

Output is empty unknown error, response code is `500 - Internal Server Error`
