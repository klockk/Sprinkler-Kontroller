/**
 *  Copyright 2015 SmartThings
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
 */
metadata {
	definition (name: "Simulated Sprinkler Timer", namespace: "klockk/testing", author: "klockk") {
		capability "Switch"
		capability "Actuator"        
        //capability "Timed Session"
        //capability "Indicator"
		//capability "Sensor"
		capability "Health Check"
	}

	// tile definitions
	tiles {
		standardTile("kontroller", "device.switch", width: 2, height: 2, canChangeIcon: true) {
        //st.Outdoor.outdoor12
        //st.valves.sprinkler
			state "off", label: '${currentValue}', action: "on", icon: "st.Outdoor.outdoor12", backgroundColor: "#ffffff", nextState: "on", defaultState: true
            state "on", label: '${currentValue}', action: "off", icon: "st.Outdoor.outdoor12", backgroundColor: "#21c621", nextState: "off"
            
            //state "running", label: '${name}', action: "start", icon: "st.Outdoor.outdoor12.on", backgroundColor: "##198e19"
            //state "stopped", label: '${name}', action: "stop", icon: "st.Outdoor.outdoor12.off", backgroundColor: "##198e19"
            //state "calceled", label: '${name}', action: "cancel", icon: "st.Outdoor.outdoor12.off", backgroundColor: "##198e19"
            //state "paused", label: '${name}', action: "pause", icon: "st.Outdoor.outdoor12.off", backgroundColor: "##198e19"
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
		}

		main "kontroller"
		details(["kontroller","refresh"])
	}
}

def installed() {
	log.trace "Executing 'installed'"
	initialize()

	// sendEvent(name: "kontroller", value: "off")
    //sendEvent(name: "kontroller", value: "stopped")
}

def updated() {
	log.trace "Executing 'updated'"
	initialize()
}

private initialize() {
	log.trace "Executing 'initialize'"
	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "healthStatus", value: "online")
	sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
}


def on() {
    log.trace "kontroller: On"
	sendEvent(name: "switch", value: "on")
}

def off() {
    log.trace "kontroller: Off"
	sendEvent(name: "switch", value: "off")
}

def refresh() {
    log.trace "Refresh: ${kontroller.currSwitch}"
	sendEvent(name: "switch", value: "refresh")
}
def start() {
    log.trace "kontroller: Started"
    sendEvent(name: "switch", value: "start")
}
def stop() {
    log.trace "kontroller: Stopped"
    sendEvent(name: "switch", value: "stop")
}

def cancel() {
    log.trace "kontroller: Canceled"
    sendEvent(name: "switch", value: "cancel")
}
def pause() {
    log.trace "kontroller: Paused"
    sendEvent(name: "switch", value: "pause")
}