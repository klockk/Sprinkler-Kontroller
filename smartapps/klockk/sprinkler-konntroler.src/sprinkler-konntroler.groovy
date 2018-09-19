/**
 *  Copyright 2018 - Around The Klock Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Author: Khile T. Klock (klockksr@gmail.com)  
 *
 *  Description:
 *     If the moisture level is below a set value & weather check indicates NOT Stormy....
 *     This will turn on a Konnected Switch(s) at a certain time every specified day.  
 *     It will then turn off each Konnected Switch after a specific amount of time.  
 *     The Weather check as well as the sensor are optional.
 **/
definition(
    name: "Sprinkler Konntroler",
    namespace: "klockk",
    version: "0.1",
    author: "Khile T. Klock",
    description: "This will create a sprinkler schedule for the specified days/time, depending on moisture from a sensor and the weather forecast",
    category: "Green Living",
    iconUrl: "http://cdn.device-icons.smartthings.com/Outdoor/outdoor12-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Outdoor/outdoor12-icn@2x.png"
    //http://cdn.device-icons.smartthings.com/Outdoor/outdoor12-icn@2x.png
  )

preferences {

    section("Choose the Virtual Timer/Kontroller? "){
		input "virtualsk", "capability.switch", title: "Virtual Sprinkler Kontroller?", required: true
	}
    
    section("On Which Days") {
        input "onDays", "enum", title: "Select Days of the Week", required: true, multiple: true, 
          options: ["Sunday":    "Sunday", 
                    "Monday":    "Monday", 
                    "Tuesday":   "Tuesday", 
                    "Wednesday": "Wednesday", 
                    "Thursday":  "Thursday", 
                    "Friday":    "Friday", 
                    "Saturday":  "Saturday"]
    }    
	section("Schedule") {
		input name: "startTime", title: "Start Time?", type: "time"
	}

    section("Relays to turn on?") {
		input "Relay1", "capability.switch", title: "Phisical Relay 1", required: false
                input "Timer1", title: "Minutes to water", type: "number", required: false, defaultValue: 0

		input "Relay2", "capability.switch", title: "Phisical Relay 2", required: false
                input "Timer2", title: "Minutes to water", type: "number", required: false, defaultValue: 0

		input "Relay3", "capability.switch", title: "Phisical Relay 3", required: false
                input "Timer3", title: "Minutes to water", type: "number", required: false, defaultValue: 0

		input "Relay4", "capability.switch", title: "Phisical Relay 4", required: false
                input "Timer4", title: "Minutes to water", type: "number", required: false, defaultValue: 0

		input "Relay5", "capability.switch", title: "Phisical Relay 5", required: false
                input "Timer5", title: "Minutes to water", type: "number", required: false, defaultValue: 0

		input "Relay6", "capability.switch", title: "Phisical Relay 6", required: false
                input "Timer6", title: "Minutes to water", type: "number", required: false, defaultValue: 0
	}
    
    section("Moisture Sensor") {
		input "sensor1", "capability.sensor", required: false
        input name: "highHumidity", title: "How Wet is too Wet (in %)?", type: "number", required: false
	}
    
    section("Zip code..."){
		input "zipcode", "text", title: "Zipcode?", required: false
	}
    
     section( "Notifications" ) {
        input("recipients", "contact", title: "Send notifications to") {
            input "sendPushMessage", "enum", title: "Send a push notification?", options: ["Yes", "No"], required: false
            input "phone1", "phone", title: "Send a Text Message?", required: false
        }
    }    
  }

def installed() {
	//log.debug "Installed with settings: ${settings}"
	log.debug "Installed with settings: "
    log.debug "    Relay1: ${Relay1} for ${Timer1} min"
    log.debug "    Relay2: ${Relay2} for ${Timer2} min"
    log.debug "    Relay3: ${Relay3} for ${Timer3} min"
    log.debug "    Relay4: ${Relay4} for ${Timer4} min"
    log.debug "    Relay5: ${Relay5} for ${Timer5} min"
    log.debug "    Relay6: ${Relay6} for ${Timer6} min"
    log.debug "    Run at: $startTime"  
    
  //initialize()
    schedule(startTime, "startTimerCallback")
    
    // Enable the ability to start the timer NOW
    state.runImmediate = false
    state.running = false
    subscribe(app.name, appHandler)
    subscribe(virtualsk, "switch", virtualskHandeler)
  }

def updated() {
	//log.debug "Updated with settings: ${settings}"
    log.debug "Updated settings: "
    log.debug "    Relay1: ${Relay1} for ${Timer1} min - ${Relay1.getId()}"
    log.debug "    Relay2: ${Relay2} for ${Timer2} min - ${Relay2.getId()}"
    log.debug "    Relay3: ${Relay3} for ${Timer3} min - ${Relay3.getId()}"
    log.debug "    Relay4: ${Relay4} for ${Timer4} min - ${Relay4.getId()}"
    log.debug "    Relay5: ${Relay5} for ${Timer5} min - ${Relay5.getId()}"
    log.debug "    Relay6: ${Relay6} for ${Timer6} min - ${Relay6.getId()}"
    log.debug "    Run at: $startTime"  

    unschedule()
    runIn(1, initialize, [overwrite: true]) // Weird, but hoping to cancel all the previous runIn()s..
    schedule(startTime, "startTimerCallback")    

    // Enable the ability to start the timer NOW
    if (!state.running) {
      setAppState(data: [runState: false])
    }
    subscribe(app, appHandler)
    subscribe(virtualsk, "switch", virtualskHandeler)
  }

def appHandler(evt) {
    log.info "app event ${evt.value} received"
    state.runImmediate = true;
    log.debug "state.runImmediate = ${state.runImmediate}"
    
    startTimerCallback()
  }

def virtualskHandeler(evt) {
  log.info "Virtual Sprinkler Kontrol event ${evt.value} received"
  if ( evt.value == "on" ) {
    state.runImmediate = true
    startTimerCallback()
  }
  else {
    runIn(1, initialize, [overwrite: true]) // Weird, but hoping to cancel all the previous runIn()s..
    setAppState(data: [runState: false])
  }
 }
 
 def cycleComplete(evt) {
  log.info "cycleComplete event ${evt.value} received"
  if ( evt.value == "off" ) {
    state.runImmediate = false
    runIn(1, initialize, [overwrite: true]) // Weird, but hoping to cancel all the previous runIn()s..
    setAppState(data: [runState: false])
  }
 }

def initialize() {
    def deviceMap = [:]
    settings.each {
        try {
            if ( it.value.hasCapability("Switch") ) {
                //log.debug "Switch: Key: $it.key / Value: $it.value / ID: ${it.value.getId()}" 
                switchName = "$it.key"
                deviceMap.put("${it.value.getId()}","${switchName}")
                "${switchName}".off()
            }
        }
        catch(Exception e) {
            //log.error "$it.value -->> $e"
        }  
    }

    deviceMap.each {
        log.debug "deviceMap: ${it.key} = ${it.value}"
    }

    //def Relay = deviceMap[1] 
    //Relay.on([delay: 100])
    //deviceMap[1].on([delay: 100])
  }
  
def setAppState(data) {
    log.debug "Setting ${app.name} Running State: ${data.data.runState}" 
    state.running=data.data.runState
    if ( state.running == true ) {
      log.debug "Flipping Sprinkler Kontroller = On"
      virtualsk.on()
      //virtualsk.running()
      }
    else {
      log.debug "Flipping Sprinkler Kontroller = Off"
      if (!state.running) {
        for (int i in 1..6) {
          turnOffSwitch("Relay${i}")
        }
      }      
      virtualsk.off()
      //virtualsk.stopped()
      state.runImmediate = false
    }
  }
 
def getSwitchById(id) {
    String keyName = null
    settings.each { 
        try {
            //def supportedCaps = it.value.capabilities
            //supportedCaps.each {cap ->
            //  log.debug "Device $it.key supports the ${cap.name} capability"
            //}
            if ( it.value.hasCapability("Switch") ) {
                //log.debug "Switch: $it.key = $it.value - ${it.value.getId()}" 
                //if ( ${it.value.getId()}.is(${id}) ) {
                if ( "${it.value.getId()}" == "${id}" ) {
                    //log.debug "FOUND $it.key "
                    keyName = "$it.key"
                }
            }
        }
        catch(Exception e) {
            //log.error "$it.value -->> $e"
        }
    }
    log.debug "getSwitchById: Returning ${keyName}"
    return keyName
  }

//def getRelay(relayNum) {
//  log.debug "relayNum: $relayNum"
//  Relay = state.deviceMap[relayNum]
//  return Relay
//}

private isStormy(json) {
    def STORMY = ['rain', 'snow', 'showers', 'sprinkles', 'precipitation']

    def forecast = json?.forecast?.txt_forecast?.forecastday?.first()
    log.info "Checking forecast response"
    if (forecast) {
        def text = forecast?.fcttext?.toLowerCase()
        if (text) {
            log.info "reponse is: ${text}"
            def result = false
            for (int i = 0; i < STORMY.size() && !result; i++) {
                result = text.contains(STORMY[i])
            }
            return result
        } else {
            return false
        }
    } else {
        log.warn "Did not get a forecast: $json"
        return false
    }
  }

def startTimerCallback() {
    log.debug "startTimerCallback: Begin"    
    //initialize() // Build a deviceMap we might be able to tap into...
    setAppState(data: [runState: true])
    
    def df = new java.text.SimpleDateFormat("EEEE")
    // Ensure the new date object is set to local time zone
    df.setTimeZone(location.timeZone)
    def day = df.format(new Date())
    //Does the preference input onDays, i.e., days-of-week, contain today?
    def dayCheck = onDays.contains(day)

    if ((dayCheck) || (state.runImmediate)) {
        sendNotificationEvent("${app.label}: Checking to see if we should Water the lawn")

        if ((!state.runImmediate) && ((sensor1 && highHumidity) && sensor1.currentHumidity > highHumidity)) {
          int hours = 48
          def yesterday = new Date(now() - (/* 1000 * 60 * 60 */ 3600000 * hours).toLong())  
          def lastHumDate = sensor1.latestState('humidity').date
          if (lastHumDate < yesterday) {
              log.warning "${app.label}: Please check sensor ${sensor1}, no humidity reports for ${hours}+ hours"
              sendNotificationEvent("${app.label}: Please check sensor ${sensor1}, no humidity reports for ${hours}+ hours")
          }
          sendNotificationEvent("${app.label}: Not Watering, because ${sensor1} is at ${sensor1.currentHumidity}")
          log.info "Not Watering, because ${sensor1} is at ${sensor1.currentHumidity}, the cut off is ${highHumidity}"
          return
        }
        if ((!state.runImmediate) && (zipcode)) {
          def response = getWeatherFeature("forecast", zipcode)
          if (isStormy(response)) {
            log.debug "Got Rain not Wattering"
            sendNotificationEvent("${app.label}: Not Watering, the forcast calls for rain.")        
            return
          }
        }
        if (sensor1 && highHumidity) {
          log.debug "The Humidity is: ${sensor1.currentHumidity} and our cut off is ${highHumidity} so we are watering."
        }

        sendNotificationEvent("${app.label}: All Checks passed, initiating Sprinkler Konntrol for ${app.label}.")
        log.debug "All Checks passed, initiating Sprinkler Konntrol for ${app.label}."

        Integer minDelay = 1  // 'Cuz we want every call to behave the same with the MAP data...    
                              // 'Cuz of this, there's a 1 minute delay between the 1st & 2nd Zone
        //if (Relay1) {
        //  runIn(minDelay,turnOnSwitch, [overwrite: false, data: [Zone: Relay1.getId(), runTimeMin: Timer1.toInteger()]] )
        //  minDelay = minDelay+Timer1.toInteger()
        //}
        //if (Relay2) {
        //  runIn(60 * minDelay ,turnOnSwitch, [overwrite: false, data: [Zone: Relay2.getId(), runTimeMin: Timer2.toInteger()]])
        //  minDelay = minDelay+Timer2.toInteger()
        //}    
        //if (Relay3) {
        //  runIn(60 * minDelay ,turnOnSwitch, [overwrite: false, data: [Zone: Relay3.getId(), runTimeMin: Timer3.toInteger()]])
        //  minDelay = minDelay+Timer3.toInteger()
        //}        
        //if (Relay4) {
        //  runIn(60 * minDelay ,turnOnSwitch, [overwrite: false, data: [Zone: Relay4.getId(), runTimeMin: Timer4.toInteger()]])
        //  minDelay = minDelay+Timer4.toInteger()
        //}        
        //if (Relay5) {
        //  runIn(60 * minDelay ,turnOnSwitch, [overwrite: false, data: [Zone: Relay5.getId(), runTimeMin: Timer5.toInteger()]])
        //  minDelay = minDelay+Timer5.toInteger()
        //}            
        //if (Relay6) {
        //  runIn(60 * minDelay ,turnOnSwitch, [overwrite: false, data: [Zone: Relay6.getId(), runTimeMin: Timer6.toInteger()]])
        //  minDelay = minDelay+Timer6.toInteger()
        //} 
        // Much Better/smaller peice of code here.. In the future, we'll figure out what 1..n really is based on preference settings..
        Integer minMultiplier = 1 // If this was 0, the first call to runIn passes the Map different, so we wait 1 second
        Integer maxZones = 0
        for (int i in 1..6) {
          if (settings."Relay${i}") {
            maxZones = i
            log.info "Scheduling " + settings."Relay${i}" + " in $minDelay Minutes"
            runIn(minMultiplier * minDelay, turnOnSwitch, [overwrite: false, data: [Zone: settings."Relay${i}".getId(), runTimeMin: settings."Timer${i}".toInteger()]] )
            minMultiplier = 60 // AFter the 1st execution, we change to 60 seconds, which converts our ## Min Delay in settings to actual MInutes for the runIn() command
            minDelay = minDelay + settings."Timer${i}".toInteger() 
          }
        }
        log.info "Scheduling setAppState([runState: false]) in $minMultiplier * $minDelay Minutes"
        runIn(minMultiplier * minDelay,setAppState, [override: false, data: [runState: false]])
        //subscribe(settings."Relay${maxZones}", "switch", cycleComplete)
    }
    else {
      log.info "${day} isn't a scheduled watering day."
    }
    log.debug "startTimerCallback: End"      
  } 

def StopTimerCallback(data) {
    //initialize()
    log.debug "StopTimerCallback: Begin"    
    //log.debug "Input data is $data"
    //log.debug "Input data.Zone is $data.Zone"
    
    // Go Get the device from settings based on the deviceId!
    String switchName = getSwitchById(data.Zone)
    //log.debug "settingName is ${switchName}"
    //log.debug "Relay is " + settings."${switchName}"
    //log.debug "Relay state is " + settings."${switchName}".currentSwitch

    turnOffSwitch("${switchName}")
    log.debug "StopTimerCallback: End"    
  }

def turnOnSwitch(data) {
    log.debug "turnOnSwitch: Begin"  
    //log.debug "Settings: ${settings}"
    //log.debug "Relay is $data.Zone"
    //log.debug "deviceMap is deviceMap[$data.Zone]"
    //log.debug "runTime is $data.runTimeMin"  
    
    // Go Get the device from settings based on the deviceId!
    String switchName = getSwitchById(data.Zone)
    //log.debug "settingName is ${switchName}"
    //log.debug "Relay is " + settings."${switchName}"
    //log.debug "Relay state is " + settings."${switchName}".currentSwitch
    
    
    if (data.Zone) {
      sendNotificationEvent("${app.label}: Watering for $data.runTimeMin minutes.")
      log.info "Watering (" + settings."${switchName}" +") for $data.runTimeMin minutes."
      if (settings."${switchName}".currentSwitch == "on") {
        log.warn "Uh Oh: ($data.Zone) is already on!?"
        log.warn "   Turning off in $data.runTimeMin minutes."
      }
      else {
        settings."${switchName}".on() // Turn on the Relay for this Zone
      }
      // Turn the Relay Back off After specified period of specified minutes
      runIn(60 * data.runTimeMin.toInteger(), StopTimerCallback, [overwrite: false, data: [Zone: data.Zone]])
    }
    log.debug "turnOnSwitch: End"    
  }

def turnOffSwitch(switchName) {
    log.debug "turnOffSwitch: Begin" 
    //log.debug "Relay is ${switchName}"
    //log.debug "Relay state is " + settings."${switchName}".currentSwitch

   if (settings."${switchName}".currentSwitch == "on") {
      log.info "Turning off " + settings."${switchName}"
      settings."${switchName}".off()
    }
   log.debug "turnOffSwitch: End"
 }

//def turnOffSwitchDelayed(Relay, msDelay) {
//    log.debug "Relay is $Relay"
//	log.debug "Relay is ${Relay.currentSwitch}"
//    log.debug "msDelay: ${msDelay}ms"
//    
//    if (Relay.currentSwitch == "on") {
//      Relay.off([delay: msDelay])
//    }
//}