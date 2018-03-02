﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using ItHappenedDomain.Domain;
using ItHappenedWebAPI.Extensions;
using ItHappenedWebAPI.Filters;
using ItHappenedWebAPI.Middlewares;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

namespace ItHappenedWebAPI
{
  public class Startup
  {
    public Startup(IConfiguration configuration)
    {
      Configuration = configuration;
    }

    public IConfiguration Configuration { get; }

    // This method gets called by the runtime. Use this method to add services to the container.
    public void ConfigureServices(IServiceCollection services)
    {
      var connectionString = "mongodb://localhost";
      var client = new MongoClient(connectionString);
      var db = client.GetDatabase("ItHappenedDB");
      var userList = new UserList(db);
      services
        .AddSingleton<UserList>(userList)
        .AddSingleton<ErrorHandlingMiddleware>();
      services.AddMvc(o =>
      {
        o.Filters.Add(new ActionFilter());
      });
    }

    // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
    public void Configure(IApplicationBuilder app, IHostingEnvironment env)
    {
      if (env.IsDevelopment())
      {
        app.UseDeveloperExceptionPage();
      }

      app.DomainErrorHandlingMiddleware();

      app.UseMvc();
    }
  }
}
