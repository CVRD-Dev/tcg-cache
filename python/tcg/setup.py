from setuptools import setup, find_packages
import os

setup(
    name='tcg-cache',
    version='1.0.0',
    author="Chase Bowlin",
    author_email="cvrddev@gmail.com",
    description="tcg tracking app",
    entry_points={
        'console_scripts': ['tcg=tcg.cli:app'],
    },
    packages=find_packages(),
    install_requires=[
        'boto3',
        'typer',
        'requests',
    ],
    include_package_data=True,
    classifiers=[
        "Programming Language :: Python :: 3",
        "Operating System :: OS Independent",
    ]
)