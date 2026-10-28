import dotenv from "dotenv";
dotenv.config();

import { Sequelize } from "sequelize";

export const sequelize = new Sequelize(
  process.env.DB_NAME || "appdb",
  process.env.DB_USER || "postgres",
  process.env.DB_PASSWORD || "postgres",
  {
    host: process.env.DB_HOST || "localhost",
    port: Number(process.env.DB_PORT) || 5432,
    dialect: "postgres",
    logging: false,

    dialectOptions:
      process.env.DB_SSL === "true"
        ? {
            ssl: {
              require: true,
              rejectUnauthorized: false
            }
          }
        : {}
  }
);
