package com.wen.android.mtabuscomparison.data.remote.bustime

data class SituationExchangeDelivery(
    val Situations: Situations
)

/*
"SituationExchangeDelivery": [
                {
                    "Situations": {
                        "PtSituationElement": [
                            {
                                "PublicationWindow": {
                                    "StartTime": "2021-02-02T00:00:00.000-05:00",
                                    "EndTime": "2021-03-04T23:59:00.000-05:00"
                                },
                                "Severity": "undefined",
                                "Summary": "Until further notice Westbound Q32 stop on 5th Ave at 56th St is closed because of NYPD activity",
                                "Description": "Until further notice Westbound Q32 stop on 5th Ave at 56th St is closed because of NYPD activity   Please use the nearby stop on 5th Ave at 59th St or at 52nd St.  Note: Please use the nearby stop on 5th Ave at 58th St or at 52nd St.",
                                "Affects": {
                                    "VehicleJourneys": {
                                        "AffectedVehicleJourney": [
                                            {
                                                "LineRef": "MTA NYCT_Q32",
                                                "DirectionRef": "1"
                                            }
                                        ]
                                    }
                                },
                                "Consequences": {
                                    "Consequence": [
                                        {
                                            "Condition": "noService"
                                        }
                                    ]
                                },
                                "CreationTime": "2021-02-02T00:00:00.000-05:00",
                                "SituationNumber": "MTA NYCT_273179"
                            },
                            {
                                "PublicationWindow": {
                                    "StartTime": "2021-02-12T00:00:00.000-05:00",
                                    "EndTime": "2021-02-14T23:59:00.000-05:00"
                                },
                                "Severity": "undefined",
                                "Summary": "Feb 12, Friday, 4 PM to 11 PM Feb 13 - 14, Sat and Sun, 11 AM to 11 PM Eastbound Q32 stops on 32nd St at Broadway, at 5th Ave and at Madison Ave are closed for NYC Open Streets Restaurants",
                                "Description": "Feb 12, Friday, 4 PM to 11 PM Feb 13 - 14, Sat and Sun, 11 AM to 11 PM Eastbound Q32 stops on 32nd St at Broadway, at 5th Ave and at Madison Ave are closed for NYC Open Streets Restaurants   Buses will make stops along 34th St and on 6th Ave.",
                                "Affects": {
                                    "VehicleJourneys": {
                                        "AffectedVehicleJourney": [
                                            {
                                                "LineRef": "MTA NYCT_Q32",
                                                "DirectionRef": "0"
                                            }
                                        ]
                                    }
                                },
                                "Consequences": {
                                    "Consequence": [
                                        {
                                            "Condition": "diverted"
                                        }
                                    ]
                                },
                                "CreationTime": "2021-02-12T00:00:00.000-05:00",
                                "SituationNumber": "MTA NYCT_273491"
                            }
                        ]
                    }
                }
            ]
 */