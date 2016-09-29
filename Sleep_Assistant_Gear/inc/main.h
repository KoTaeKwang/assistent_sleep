/*
 * main.h
 *
 *  Created on: Jul 31, 2016
 *      Author: KimNam
 */

#ifndef MAIN_H_
#define MAIN_H_

#define TAG "sleep_assistant_gear"

#include <app.h>
#include <Elementary.h>
#include <system_settings.h>
#include <efl_extension.h>
#include <dlog.h>
#include <sensor.h>
#include <glib.h>

typedef struct appdata {
	Evas_Object *win;
	Evas_Object *conform;
	Evas_Object *naviframe;
	Evas_Object *layout;
	Evas_Object *datetime;
	Evas_Object *popup;
	Evas_Object *button;
	Eext_Circle_Surface *circle_surface;
	double currentHRM;
	int HRM50;
	int HRM75;
	int check;
	Evas_Object *HRMLabel;
	Evas_Object *check50Label;
	Evas_Object *check75Label;
	Evas_Object *peekLabel;
	Evas_Object *accerlerateLabel;
	struct tm saved_time;
} appdata_s;

typedef struct _sensor_info
{
	sensor_h sensor; /**< Sensor handle */
	sensor_listener_h sensor_listener;
} sensorinfo;

static sensorinfo HRM_info;
static sensorinfo accerlerate_info;

void _HRM_value(sensor_h sensor, sensor_event_s *sensor_data, void *user_data);
void start_heartrate_sensor(appdata_s *ad);

void _accerleration_value(sensor_h sensor, sensor_event_s *sensor_data, void *user_data);
void start_acceleration_sensor(appdata_s *ad);

void alarm_cb(void *data, Evas_Object * obj, void *event_info);
void status_cb(void *data, Evas_Object * obj, void *event_info);

void initialize_sap();
void update_ui(char *data);
gboolean send_data(char *message);

#ifdef  LOG_TAG
#undef  LOG_TAG
#endif
#define LOG_TAG "sleep_assistant_gear"

#if !defined(PACKAGE)
#define PACKAGE "org.tizen.sleep_assistant_gear"
#endif

#endif /* MAIN_H_ */
