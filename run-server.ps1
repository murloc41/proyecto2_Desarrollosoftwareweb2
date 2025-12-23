#!/bin/bash
cd g:\Compendium
.\mvnw.cmd spring-boot:run 2>&1 | head -100
