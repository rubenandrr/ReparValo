-- ====================================================================
-- ReparValo Database Seeding Script (Swiss Market Context)
-- 
-- This script pre-populates the in-memory H2 database on application startup.
-- It inserts a curated set of vehicles and common body parts with 
-- representative valuation metrics and repair hours for the Geneva area.
-- 
-- All prices and valuations are defined in Swiss Francs (CHF).
-- ====================================================================

-- ====================================================================
-- 1. Seed Vehicles Table
-- Preloads common vehicle models used to calculate trade-in values.
-- - base_value_chf: Theoretical new price in Switzerland.
-- - depreciation_rate: Annual percentage reduction in value.
-- ====================================================================

-- Peugeot 208 (Subcompact hatch, popular everyday car)
INSERT INTO vehicles (make, model, base_value_chf, depreciation_rate) 
VALUES ('Peugeot', '208', 22000.0, 0.15);

-- Renault Clio (French bestseller, holds average value)
INSERT INTO vehicles (make, model, base_value_chf, depreciation_rate) 
VALUES ('Renault', 'Clio', 20000.0, 0.14);

-- Tesla Model 3 (High-volume electric sedan, higher initial depreciation)
INSERT INTO vehicles (make, model, base_value_chf, depreciation_rate) 
VALUES ('Tesla', 'Model 3', 46000.0, 0.18);

-- Volkswagen Golf (German benchmark, retains value well in Switzerland)
INSERT INTO vehicles (make, model, base_value_chf, depreciation_rate) 
VALUES ('Volkswagen', 'Golf', 32000.0, 0.12);


-- ====================================================================
-- 2. Seed Car Parts Table
-- Preloads standard body panels and components for damage selection.
-- - name: SVG-matching identifier.
-- - new_price_chf: Swiss distributor cost for brand new replacement parts.
-- - used_price_chf: Average recycling yard cost for second-hand parts.
-- - repair_hours: Estimated mechanic labor hours required for swapping/painting.
-- ====================================================================

-- Right side doors
INSERT INTO car_parts (name, new_price_chf, used_price_chf, repair_hours) 
VALUES ('rear_right_door', 550.0, 180.0, 3.5);
INSERT INTO car_parts (name, new_price_chf, used_price_chf, repair_hours) 
VALUES ('front_right_door', 600.0, 200.0, 4.0);

-- Left side doors
INSERT INTO car_parts (name, new_price_chf, used_price_chf, repair_hours) 
VALUES ('rear_left_door', 550.0, 180.0, 3.5);
INSERT INTO car_parts (name, new_price_chf, used_price_chf, repair_hours) 
VALUES ('front_left_door', 600.0, 200.0, 4.0);

-- Bumpers
INSERT INTO car_parts (name, new_price_chf, used_price_chf, repair_hours) 
VALUES ('front_bumper', 450.0, 150.0, 2.5);
INSERT INTO car_parts (name, new_price_chf, used_price_chf, repair_hours) 
VALUES ('rear_bumper', 480.0, 160.0, 2.5);

-- Headlights (LED/Matrix options cost more in Switzerland)
INSERT INTO car_parts (name, new_price_chf, used_price_chf, repair_hours) 
VALUES ('left_headlight', 320.0, 110.0, 1.0);
INSERT INTO car_parts (name, new_price_chf, used_price_chf, repair_hours) 
VALUES ('right_headlight', 320.0, 110.0, 1.0);

-- Side mirrors
INSERT INTO car_parts (name, new_price_chf, used_price_chf, repair_hours) 
VALUES ('left_mirror', 220.0, 75.0, 0.8);
INSERT INTO car_parts (name, new_price_chf, used_price_chf, repair_hours) 
VALUES ('right_mirror', 220.0, 75.0, 0.8);

-- Hood & Trunk panels (higher labor hours due to alignment and size)
INSERT INTO car_parts (name, new_price_chf, used_price_chf, repair_hours) 
VALUES ('hood', 700.0, 250.0, 3.0);
INSERT INTO car_parts (name, new_price_chf, used_price_chf, repair_hours) 
VALUES ('trunk', 750.0, 270.0, 3.0);