# sprack: spray rack handler for jruby apps

* run rails, sinatra or any other rack compatible (j)ruby stack
* This is for threadsafe apps only!
* if using activerecord, make sure to set `:allow_concurrency => true, :pool => <many>` 


## develop

```

rbenv local jruby-1.7.4
gem install bundler
bundle install
bundle exec sbt
> test

```

## build

```

bundle exec rake sprack:gem
...
...
[info] Packaging /github.com/sclasen/sprack/target/scala-2.10/sprack.jar ...
[info] Done packaging.

```

## use

```

Gemfile

gem "sprack"


bundle exec sprack --help

0.0.9
  -a, --akkafile  <arg>
  -h, --host  <arg>        (default = 0.0.0.0)
  -p, --port  <arg>        (default = 8080)
  -healthport <arg>        (default none)
  -r, --rackfile  <arg>    (default = ./config.ru)
  -t, --timeout  <arg>     (default = 30)
      --help              Show help message
      --version           Show version of this program

```

## initial benchmark

https://gist.github.com/sclasen/26b0b013273bf9d564e9


